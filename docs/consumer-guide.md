# casehub-worker — Consumer Guide

> Foundation-tier automated task primitives — Worker, WorkerFunction, Capability, execution policy.

**Repo:** [casehubio/casehub-worker](https://github.com/casehubio/casehub-worker)
**Tier:** Foundation

---

## Overview

`casehub-worker` is the peer of `casehub-work` — while `casehub-work` owns human task lifecycle (`WorkItem`), `casehub-worker` owns the identity, function, and capability vocabulary for automated workers.

Extracted from `casehub-engine-api` so that Workers are a shareable foundation primitive — `casehub-desiredstate` and other downstream consumers can depend on `casehub-worker-api` without pulling in the full engine.

---

## Modules to Depend On

| Module | artifactId | What it is |
|--------|-----------|------------|
| `api/` | `casehub-worker-api` | Pure-Java value types + `WorkerFunction<T>` interface. Depends on `casehub-platform-api` for `ExecutionPolicy`. No Quarkus, no JPA. |
| `runtime/` | `casehub-worker` | `DefaultWorkerExecutor` — capability-aware execution with typed input support, JSON Schema validation, policy enforcement, and OTel instrumentation. |
| `testing/` | `casehub-worker-testing` | `MockWorkerExecutor` + `TestWorkerBuilder` — `@QuarkusTest` isolation. |

---

## Key Types

| Type | Kind | Purpose |
|------|------|---------|
| `Worker` | record | Named automated task with `capabilityNames`, function, execution policy, description. Builder supports `capabilityName(String)`, `capabilityNames(String...)`, `noFunction()`, `<T>fn()` (typed builder entry point). |
| `Capability` | record | Named capability tag with input/output schema — used for routing and validation. Fields: `name`, `inputSchema`, `outputSchema`, `description`. |
| `WorkerFunction<T>` | interface (generic) | Parameterised by input type `T`. Inner `Sync<T>` record for typed execution. `None` record implements `WorkerFunction<Void>`. Static `NONE` constant. |
| `TypedFunctionBuilder<T>` | class | Builder helper for type-safe function binding: `builder.<MyPojo>fn().apply(pojo -> ...)`. Creates `WorkerFunction.Sync` with the runtime type. |
| `WorkerResult` | record | `output` (`Map<String, Object>`) + `outcome` (`WorkerOutcome`). Factory methods: `of(output)`, `of(output, PlannedAction)`, plus `declined`, `failed`, `expired` — each with a partial-output overload. |
| `WorkerOutcome` | sealed interface | `Success(PlannedAction)`, `Declined(String reason)`, `Failed(String reason)`, `Expired(String reason)` |
| `PlannedAction` | record | Structured follow-on action — `description`, `actionType`, `parameters` (Map). Returned via `WorkerOutcome.Success(PlannedAction)`. |

---

## Dependency Rules

```
casehub-worker-api  →  casehub-platform-api (ExecutionPolicy only — pure Java, no Quarkus)
casehub-worker      →  casehub-worker-api, casehub-platform-governance, quarkus-arc, opentelemetry-api, json-schema-validator
casehub-worker-testing → casehub-worker-api
```

**Add to consumers:**
```xml
<!-- Compile dep -->
<dependency>
  <groupId>io.casehub</groupId>
  <artifactId>casehub-worker-api</artifactId>
</dependency>

<!-- Test scope -->
<dependency>
  <groupId>io.casehub</groupId>
  <artifactId>casehub-worker-testing</artifactId>
  <scope>test</scope>
</dependency>
```

Version managed by `casehub-parent` BOM (`version.io.casehub.worker`).

---

## Structural Notes

- `api/` depends only on `casehub-platform-api` (for `ExecutionPolicy`) — no Quarkus, no JPA, safe in any Java module
- `WorkerFunction<T>` implementations live in consuming repos (`AgentWorkerFunction`, `FlowWorkerFunction` in `casehub-engine-api`)
- `MockWorkerExecutor` is `@DefaultBean @ApplicationScoped` — displaced by the runtime `DefaultWorkerExecutor` when present. Mirrors validation guards (capability membership, Sync-only, input type) but skips schema validation and policy enforcement.
