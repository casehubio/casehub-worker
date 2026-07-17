# Async WorkerFunction — Non-Blocking Worker Execution

**Issue:** #5
**Date:** 2026-07-17

## Problem

WorkerFunction only has a Sync variant. I/O-bound workers (API calls, database queries, external service calls) block the executor thread. The executor returns WorkerResult synchronously, forcing callers to block even when the underlying work is naturally asynchronous.

Additionally, DefaultWorkerExecutor depends on PolicyEnforcer from casehub-platform-governance — a hand-rolled retry/timeout implementation that duplicates SmallRye Fault Tolerance, blocks threads during retry backoff via Thread.sleep(), and cannot compose with asynchronous actions.

## Design

### WorkerFunction.Async\<T\>

New record variant on WorkerFunction, mirroring Sync but with CompletionStage return:

```java
record Async<T>(Class<T> inputType, Function<T, CompletionStage<WorkerResult>> fn)
        implements WorkerFunction<T> {
    public Async {
        Objects.requireNonNull(inputType, "inputType must not be null");
        Objects.requireNonNull(fn, "fn must not be null");
    }
}
```

CompletionStage is a JDK standard type — appropriate for the API module, which should not depend on Mutiny. Sync and None are unchanged.

### WorkerExecutor Return Type

```java
public interface WorkerExecutor {
    Uni<WorkerResult> execute(Worker worker, Capability capability, Object input);
}
```

Breaking change — all call sites update. Pre-release, no consumers outside this repo and the engine.

Uni (SmallRye Mutiny) is the Quarkus-native reactive type. The runtime module already depends on Quarkus CDI and SmallRye FT, so Mutiny is transitively available — no new dependency. CompletionStage stays in the API (WorkerFunction.Async); the runtime lifts it into Uni internally via `Uni.createFrom().completionStage()`.

### Unified Executor Pipeline

One code path for both Sync and Async:

1. **Validate** — null checks, capability membership, input type match. Programming errors throw immediately (never inside the Uni).
2. **Schema validate input** — fail fast with `Uni.createFrom().item(WorkerResult.failed(...))` if invalid.
3. **Lift to Uni** — Sync: `Uni.createFrom().item(() -> syncFn.apply(input)).runSubscriptionOn(virtualThreads)` — offloads the blocking function to a virtual thread so Guard's timeout race can fire (without offload, the sync supplier blocks the subscribing thread and the timeout timer cannot interrupt it). Async: `Uni.createFrom().completionStage(() -> asyncFn.apply(input))` — already non-blocking, no offload needed. `DefaultWorkerExecutor` injects `@VirtualThreads ExecutorService` for this purpose, aligning with the engine's `SyncAgentWorkerFunctionHandler` pattern.
4. **Guard** — SmallRye FT Guard built from ExecutionPolicy: `guard.call(() -> liftedUni, Uni.class)`. The `Uni.class` type parameter tells Guard to treat the action as asynchronous and apply timeout/retry around the Uni subscription. This is a single code path — Guard handles both sync-in-Uni and async-in-Uni uniformly.
5. **Schema validate output** — in `.map()` on the result Uni. Success only, warn on mismatch.
6. **OTel** — Scope and Span have separate lifecycles (see OTel section below).
7. **Exception mapping** — in `.onFailure().recoverWithItem()`: `TimeoutException` → `WorkerResult.expired()`, other exceptions → `WorkerResult.failed()`.

### SmallRye FT Guard Replaces PolicyEnforcer

PolicyEnforcer is removed. Guard is constructed from ExecutionPolicy:

- `timeoutMs` → `withTimeout().duration(timeoutMs, ChronoUnit.MILLIS)`
- `retries.maxAttempts` → `withRetry().maxRetries(maxAttempts - 1)` (Guard counts retries, not attempts)
- `retries.delayMs` → `withRetry().delay(delayMs, ChronoUnit.MILLIS)`
- `retries.backoffStrategy`:
  - FIXED → default constant delay (no backoff builder needed — Guard uses constant delay by default)
  - EXPONENTIAL → `withRetry().withExponentialBackoff().done()`
  - EXPONENTIAL_WITH_JITTER → `withRetry().withExponentialBackoff().done().jitter(delayMs, ChronoUnit.MILLIS)` — jitter is set via `RetryBuilder.jitter()`, which adds random jitter in range `[-jitter, +jitter]` to the computed delay

**Jitter semantics change:** Guard's `jitter()` applies a fixed bound regardless of attempt number, while the current `RetryPolicies.computeBackoffDelayMs()` uses proportional jitter (`ThreadLocalRandom.nextLong(exponentialDelay + 1)`) that grows with each attempt. This is an intentional change: Guard implements MicroProfile Fault Tolerance standard jitter; the current proportional jitter was a non-standard PolicyEnforcer/RetryPolicies implementation. Existing tests with `EXPONENTIAL_WITH_JITTER` backoff may need adjusted timing expectations.
- `retries.maxDelayMs` → `withRetry().maxDelay(maxDelayMs, ChronoUnit.MILLIS)` (applies to exponential backoff cap); for exponential backoff: `withExponentialBackoff().maxDelay(maxDelayMs, ChronoUnit.MILLIS).done()`

Guard instances cached in `ConcurrentHashMap<ExecutionPolicy, Guard>` — ExecutionPolicy is a record with free equals/hashCode. This cache is safe because the current configuration uses only timeout and retry, both of which are per-invocation (no shared state). When stateful strategies are added (circuit breaker, bulkhead, rate limit), the cache key must include worker identity (e.g. worker name + ExecutionPolicy) to prevent cross-worker state sharing.

**Retry ownership:** Guard retry applies to standalone worker module usage only. When the engine dispatches workers, retry is handled durably by QuartzRetryService at the scheduler level — the engine's DefaultWorkerExecutor dispatches via WorkerFunctionHandler SPI and does not use Guard. The two retry mechanisms operate in separate executor hierarchies and cannot produce N×M retry multiplication.

### OTel Span Lifecycle

Scope and Span are distinct OTel concepts with different lifecycle rules:

**Scope** (thread-local, from `span.makeCurrent()`):
- Opened synchronously on the dispatching thread via try-with-resources
- Closed synchronously on the same thread before the async boundary
- Sets the span as "current" for the dispatching thread during function setup

**Span** (explicit lifecycle):
- Opened before function dispatch (step 3)
- Ended in `.onTermination()` callback on the Uni, on whatever thread completes
- For sync workers (offloaded to virtual thread), terminates when the virtual thread completes — effectively the same timing as today's try-finally for fast functions
- For async workers, stays open across the async boundary; duration reflects actual execution time
- Outcome attribute, timeout events, and error recording all happen in the termination callback

**Async context propagation:** The span is NOT the current span on the async execution thread. If the worker function needs OTel tracing context on the async thread, it must capture `Context.current()` before dispatch and restore it via `context.makeCurrent()` in the async body. This is standard OTel async practice — not specific to this design.

### Worker.Builder

New async convenience methods:

- `asyncFunction(Function<Map<String, Object>, CompletionStage<WorkerResult>>)` — untyped async, mirrors existing `function(Function<Map, WorkerResult>)`
- `TypedFunctionBuilder.applyAsync(Function<T, CompletionStage<WorkerResult>>)` — typed async, mirrors `apply()`

Usage: `Worker.builder().name("x").capabilityName("x").<MyType>fn().applyAsync(req -> processAsync(req)).build()`

### MockWorkerExecutor

Return type changes to `Uni<WorkerResult>`. Handles both Sync and Async via pattern matching on the function variant. No policy enforcement, no OTel, no schema validation — same test-double philosophy as today.

### TestWorkerBuilder

New factories: `async(name, fn)`, `asyncWithCapability(name, fn)`, `asyncWithCapability(name, inputSchema, outputSchema, fn)` — mirror the existing sync factories.

### Error Handling

Three-category model preserved, adapted for Uni:

**Programming errors** — thrown immediately, never inside the Uni:
- Null capability, capability not in worker, input type mismatch, malformed schema, non-Sync/Async function

**Worker-level conditions** — resolved inside the Uni via `.onFailure().recoverWithItem()`:
- `TimeoutException` → `WorkerResult.expired()`
- Retry exhaustion → `WorkerResult.failed()` with original message
- Worker function exception → `WorkerResult.failed()`
- Worker returns Declined/Failed/Expired → pass through

**Infrastructure signals** — propagate as failed Uni:
- InterruptedException, JVM errors

**Async dispatch edge case:** If the async function throws during dispatch (before returning a CompletionStage), caught and mapped to `WorkerResult.failed()`. If the function returns null instead of a CompletionStage, `Uni.createFrom().completionStage((CompletionStage<WorkerResult>) null)` produces a failure that maps to `WorkerResult.failed()`.

## Engine Integration

The engine's `DefaultWorkerExecutor` dispatches via the `WorkerFunctionHandler` SPI (`handler.supports(function)` → `handler.execute(...)`). Adding `WorkerFunction.Async` to the worker module's API means the engine needs a corresponding handler — an `AsyncWorkerFunctionHandler` — or the engine will throw `UnsupportedOperationException` when encountering an Async function. This handler is a separate concern in `casehub-engine`, not in scope for this spec but required before the engine can dispatch async functions.

The worker module's `DefaultWorkerExecutor` handles both Sync and Async independently — it does not depend on the engine's handler SPI.

## Executor Convergence Direction

The platform has two `WorkerExecutor` hierarchies: the worker module's (standalone, Guard-based) and the engine's (lifecycle-aware, handler-dispatched via `WorkerFunctionHandler` SPI). This spec narrows the gap by aligning on `Uni` return types and SmallRye ecosystem tooling. Full convergence — the engine delegating function invocation to the worker module's executor while keeping engine-specific concerns (WorkerContext, virtual threads, Quartz retry) in the handler — is a viable future direction but a separate design effort.

## Dependency Changes

- **Add:** `quarkus-smallrye-fault-tolerance` (runtime module) — for SmallRye FT Guard programmatic API. SmallRye Mutiny is transitively available via this dependency.
- **Remove:** `casehub-platform-governance` (runtime module) — PolicyEnforcer no longer used
- **Keep:** `casehub-platform-api` — ExecutionPolicy, RetryPolicy, BackoffStrategy are domain config types that stay on Worker

## Modules Affected

| Module | Changes |
|--------|---------|
| api | WorkerFunction.Async record, TypedFunctionBuilder.applyAsync, Worker.Builder.asyncFunction |
| runtime | WorkerExecutor return type (Uni), DefaultWorkerExecutor unified pipeline + `@VirtualThreads ExecutorService` injection, Guard replaces PolicyEnforcer |
| testing | MockWorkerExecutor async support (Uni return), TestWorkerBuilder async factories |

## Out of Scope

- WorkerContext (#4) — if it lands later, the async signature can incorporate it
- Async-specific timeout configuration (separate from ExecutionPolicy timeout) — follow-up if needed
- Circuit breaker / bulkhead on ExecutionPolicy — Guard supports them; add to ExecutionPolicy when needed (cache key must be updated per Guard section above)
- Engine `AsyncWorkerFunctionHandler` — separate concern in casehub-engine (see Engine Integration above)
- Concurrency control / backpressure for async workers — callers control concurrency via their thread pool or calling pattern; Guard's bulkhead is the natural mechanism when needed, listed above
