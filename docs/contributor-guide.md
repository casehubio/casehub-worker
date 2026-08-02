# casehub-worker — Contributor Guide

> Architecture, execution model, and internals for platform builders modifying casehub-worker.

**Repo:** [casehubio/casehub-worker](https://github.com/casehubio/casehub-worker)

---

## Module Structure

| Module | artifactId | Detail |
|--------|-----------|--------|
| `api/` | `casehub-worker-api` | Pure-Java value types + `WorkerFunction<T>` interface. Depends on `casehub-platform-api` for `ExecutionPolicy`. No Quarkus, no JPA — safe in any Java module. |
| `runtime/` | `casehub-worker` | `DefaultWorkerExecutor` (`@ApplicationScoped`) — capability-aware execution with typed input support, JSON Schema validation (`SchemaValidator`), policy enforcement (`PolicyEnforcer`), and OTel instrumentation. Dependencies: `casehub-worker-api`, `casehub-platform-governance`, `quarkus-arc`, `opentelemetry-api`, `json-schema-validator`. |
| `testing/` | `casehub-worker-testing` | `MockWorkerExecutor` (`@DefaultBean @ApplicationScoped` — mirrors validation guards but skips schema/policy enforcement), `TestWorkerBuilder` (`syncWithCapability()` convenience with optional custom schemas, `WorkerWithCapability` record) — `@QuarkusTest` isolation. Depends on `casehub-worker-api` only. |

---

## Execution Model

`WorkerExecutor` interface: `WorkerResult execute(Worker worker, Capability capability, Object input)` — third parameter is `Object` (not `Map`), supporting typed POJO inputs via `WorkerFunction<T>`.

`DefaultWorkerExecutor` (`@ApplicationScoped`) performs, in order:

1. Null check on capability
2. Capability membership check: `worker.capabilityNames().contains(capability.name())`
3. Sync-only check: only `WorkerFunction.Sync` supported
4. Input type check: `sync.inputType().isInstance(input)` — rejects mismatched types
5. Schema parsing: `schemaValidator.ensureSchemaParsed()` on both input and output schemas (fail-fast on malformed schemas)
6. OTel span: `worker.execute` with `worker.name` and `worker.capability` attributes
7. Input schema validation — if invalid, returns `WorkerResult.failed(error)` without calling the function
8. Function execution via `PolicyEnforcer.execute()` (retries, timeout per `ExecutionPolicy`)
9. Output schema validation (on `Success` only) — **warn-only**: logs but returns success
10. `TimeoutPolicyException` maps to `WorkerResult.expired()`; other exceptions map to `WorkerResult.failed()`

### SchemaValidator

`@ApplicationScoped` — uses `com.networknt.json-schema-validator` with JSON Schema 2020-12. Caches parsed schemas in `ConcurrentHashMap`. Empty schema `"{}"` treated as skip-validation. Validates by converting input/output to `JsonNode` via Jackson.

### Input vs Output Validation Asymmetry

Input schema validation is **blocking** — the function is never called if input is invalid. Output schema validation is **warn-only** — logs but returns success. This is intentional: prevent bad data from entering while not breaking workers with evolving output schemas.

---

## Consumed By

| Repo | Module | What it uses |
|------|--------|-------------|
| `casehub-engine` | `runtime` | `Worker`, `Capability`, `WorkerFunction` — execution path |
| `casehub-desiredstate` | `runtime` | `Worker`, `Capability` — node provisioning in desiredstate graph |

---

## Current State

- Governance types (`ExecutionPolicy`, `RetryPolicy`, `BackoffStrategy`) live in `casehub-platform-governance`, not here
- `WorkerFunction<T>` implementations live in consuming repos (`AgentWorkerFunction`, `FlowWorkerFunction` in `casehub-engine-api`)
- `MockWorkerExecutor` is `@DefaultBean` — displaced by runtime `DefaultWorkerExecutor` when present
