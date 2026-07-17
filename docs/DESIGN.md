# CaseHub Worker — Design

## Architecture

_To be documented._

## Module Structure

| Module | Type | Purpose |
|--------|------|---------|
| `api` | API | Worker, WorkerFunction, Capability, WorkerResult, WorkerOutcome, PlannedAction |
| `runtime` | Runtime | WorkerExecutor with SmallRye FT Guard + OTel tracing |
| `testing` | Test support | MockWorkerExecutor + TestWorkerBuilder |

## Key Abstractions

- **PlannedAction** — a consequential action a worker intends to take. Carries `description` (human-readable summary), `actionType` (machine-readable identifier), and `parameters` (action arguments). Lives on `WorkerOutcome.Success` — the type system structurally enforces that only successful outcomes can declare an action. Identity (workerId, caseId) is NOT on PlannedAction — it belongs to the orchestration tier's `ClassificationContext`.
- **WorkerOutcome** — sealed interface with four variants: `Success(PlannedAction)`, `Declined(reason)`, `Failed(reason)`, `Expired(reason)`. PlannedAction on Success is nullable (most successes don't declare actions).

## Schema Validation

`SchemaValidator` (`@ApplicationScoped`, runtime module) validates worker inputs and outputs against JSON Schema documents declared on `Capability.inputSchema()` and `Capability.outputSchema()`. Uses `com.networknt:json-schema-validator` 1.0.83 (Draft 2020-12).

| Direction | Invalid result | Rationale |
|-----------|---------------|-----------|
| Input | `WorkerResult.failed()` — function never runs | Prevents wasting compute on garbage input |
| Output (Success only) | Log WARN, return result unchanged | Worker did its job; schema mismatch is observability |

- Empty schema `"{}"` skips validation (performance shortcut — empty JSON Schema validates everything)
- Malformed schema throws `IllegalArgumentException` (programming error, same category as null capability)
- Schema objects are cached in a `ConcurrentHashMap` keyed by schema string
- `Declined`/`Failed`/`Expired` outcomes are excluded from output validation — partial output is diagnostic, not a contract

## WorkerFunction Variants

`WorkerFunction<T>` is a sealed-ish interface with three variants:

- **`Sync<T>`** — `Function<T, WorkerResult>`. Synchronous execution.
- **`Async<T>`** — `Function<T, CompletionStage<WorkerResult>>`. Non-blocking execution. CompletionStage (JDK standard) keeps the API module framework-agnostic.
- **`None`** — no-function placeholder (`inputType() = Void.class`).

Both `Sync` and `Async` carry `Class<T> inputType()` for runtime type checking.

## Execution Contract

`WorkerExecutor.execute()` returns `Uni<WorkerResult>`. Programming errors throw immediately (never inside the Uni). Worker-level conditions resolve inside the Uni. Infrastructure signals propagate as failed Uni.

The executor uses a unified pipeline for both Sync and Async: validate → schema check input → lift to Uni → Guard (fault tolerance) → schema check output → OTel span close.

### Programming errors

| Error | Exception | When |
|-------|-----------|------|
| Null capability | `NullPointerException` | `capability` is null |
| Capability not in worker | `IllegalArgumentException` | `capability.name()` not in `worker.capabilityNames()` |
| Non-Sync/Async function | `UnsupportedOperationException` | `worker.function()` is not `Sync` or `Async` |
| Input type mismatch | `IllegalArgumentException` | `input` is not an instance of `WorkerFunction.inputType()` |
| Null input | `IllegalArgumentException` | `input` is null (subcase of type mismatch — `isInstance(null)` is false) |
| Malformed schema | `IllegalArgumentException` | `Capability.inputSchema()` or `outputSchema()` is not valid JSON Schema |

### Worker-level outcomes

| Exception source | WorkerOutcome |
|-----------------|---------------|
| MP FT `TimeoutException` | `Expired` |
| Retry exhaustion | `Failed` (original exception message) |
| Raw worker exception | `Failed` |
| `InterruptedException` | propagates (infrastructure) |

OTel: timeout-to-Expired records a `worker.timeout` event (not `StatusCode.ERROR`). All other exception paths set `StatusCode.ERROR` and record the exception. All paths set the `worker.outcome` span attribute. Span is opened before dispatch and closed in `onTermination` callback — for async workers, the span stays open across the async boundary.

## Fault Tolerance

`DefaultWorkerExecutor` uses SmallRye Fault Tolerance `Guard` (programmatic API) for retry and timeout enforcement. Guard instances are cached by `ExecutionPolicy` (record identity). `ExecutionPolicy` config maps to Guard builder calls:

- `timeoutMs` → `withTimeout().duration(...)`
- `retries.maxAttempts` → `withRetry().maxRetries(maxAttempts - 1)`
- `retries.delayMs` → `withRetry().delay(...)`
- `retries.backoffStrategy` → `withExponentialBackoff()` + optional jitter

Guard replaces the former `PolicyEnforcer` from `casehub-platform-governance`. The `casehub-platform-governance` dependency is removed from the runtime module.

## SPI Contracts

_To be documented._

## Data Model

_To be documented._

## Configuration

_To be documented._
