# PlannedAction + Enriched WorkerResult Factories — worker#2

**Date:** 2026-06-23
**Issue:** casehubio/casehub-worker#2
**Upstream:** casehubio/engine#543 (worker primitives migration)
**Status:** Approved

## Summary

Add `PlannedAction` record to worker-api and enrich `WorkerResult`/`WorkerOutcome` with factories that support planned actions and partial output. These changes are prerequisites for engine#543, which migrates Worker primitives from engine-api to the foundation-tier worker-api.

## Design Decisions

### PlannedAction on Success, not on WorkerResult

A PlannedAction is only valid with a Success outcome. The engine currently enforces this with a runtime validation rule in WorkerResult's compact constructor (`if (!(outcome instanceof Success) && plannedAction != null) throw`). The right design makes this structurally impossible — PlannedAction lives on `WorkerOutcome.Success`, not as a nullable field on `WorkerResult`. The type system enforces the constraint; no runtime validation needed.

This changes the access pattern at every consumption site. Currently the engine does `workerResult.plannedAction()` — a direct accessor on a 3-component record. After migration, callers must pattern-match:

```java
if (workerResult.outcome() instanceof WorkerOutcome.Success s) {
    s.plannedAction();
}
```

This is actually better — it forces callers into a success-handling branch before touching the action. The structural enforcement replaces the runtime check, and the engine's `WorkerResultExpiredTest.expired_outcome_rejects_planned_action()` test becomes dead code (the compiler prevents the scenario it tests).

### Nullable PlannedAction, not a split variant

`Success(PlannedAction plannedAction)` with nullable is preferred over splitting into `Success()` / `SuccessWithAction(PlannedAction)`. The split would add a fifth variant to the sealed hierarchy, doubling success-path matching in every switch expression. Most callers don't care about the distinction — they just want "did it succeed?" The nullable approach serves the 95% case cleanly.

### Field naming — `description` and `parameters`

The engine's existing `PlannedAction` uses `description` for the human-readable summary field. The engine spec (engine#543) prescribed `action`, but `action` on a record called `PlannedAction` is a tautology — the record IS the action. Every engine call site confirms the value is descriptive text: `"File SAR report"`, `"File resolution"`, `"Do something"`. `description` is unambiguous. Foundation-tier extraction is the moment to get the name right.

The engine's existing field `context` is renamed to `parameters`. The values are action arguments (`"accountId"`, `"amount"`), not ambient context. `context` in the engine codebase denotes runtime context (WorkerContext, ClassificationContext) — using the same word for action parameters is a misnomer. `parameters` is precise.

### Enrichment story — identity stays off PlannedAction

The engine's current `PlannedAction` carries `workerId` and `caseId`, populated via `withIdentity()` before passing to `ActionRiskClassifier`. The foundation-tier type intentionally strips these — identity is an engine routing concern, not a worker declaration concern.

The migration path is defined in engine#543: identity moves to a new `ClassificationContext` record in engine-api. The classifier SPI changes from `classify(PlannedAction)` to `classify(PlannedAction, ClassificationContext)`. `QuartzWorkerExecutionJob.onSuccess()` stops calling `withIdentity()` entirely — the context is constructed at the classifier call site from state already available there.

This eliminates the two-state problem in the current design (enriched vs. unenriched PlannedAction instances of the same type). PlannedAction is a pure declaration: "I want to do X with these parameters." Identity is routing context: "which worker in which case is making this declaration." These are orthogonal concerns that belong on separate types.

### No defensive copy of parameters

`PlannedAction.parameters` follows the existing convention: transient data maps (`WorkerResult.output`) are not copied; structural metadata (`Worker.capabilities`) is.

## Changes

### 1. PlannedAction record (new file)

`api/src/main/java/io/casehub/worker/api/PlannedAction.java`

```java
public record PlannedAction(String description, String actionType, Map<String, Object> parameters) {
    public PlannedAction {
        Objects.requireNonNull(description);
        Objects.requireNonNull(actionType);
        if (parameters == null) parameters = Map.of();
    }

    public static PlannedAction of(String description, String actionType) {
        return new PlannedAction(description, actionType, Map.of());
    }

    public static PlannedAction of(String description, String actionType, Map<String, Object> parameters) {
        return new PlannedAction(description, actionType, parameters);
    }
}
```

- `description` and `actionType` required (non-null). `parameters` defaults to empty map — never null after construction.
- Two factory overloads: two-arg (convenience, no parameters) and three-arg (all fields).

### 2. WorkerOutcome.Success gains PlannedAction

```java
public sealed interface WorkerOutcome {
    static WorkerOutcome success() { return new Success(null); }
    static WorkerOutcome success(PlannedAction action) {
        Objects.requireNonNull(action);
        return new Success(action);
    }

    record Success(PlannedAction plannedAction) implements WorkerOutcome {}
    record Declined(String reason) implements WorkerOutcome {}
    record Failed(String reason) implements WorkerOutcome {}
    record Expired(String reason) implements WorkerOutcome {}
}
```

- `success()` factory updated: `new Success()` → `new Success(null)`.
- New `success(PlannedAction)` factory for symmetry.
- Sealed hierarchy unchanged — still four variants.

### 3. WorkerResult — success-with-action factory

```java
public static WorkerResult of(Map<String, Object> output, PlannedAction action) {
    Objects.requireNonNull(action);
    return new WorkerResult(output, new WorkerOutcome.Success(action));
}
```

### 4. WorkerResult — partial-output factory overloads

```java
public static WorkerResult declined(String reason, Map<String, Object> partialOutput) {
    return new WorkerResult(partialOutput, new WorkerOutcome.Declined(reason));
}
public static WorkerResult failed(String reason, Map<String, Object> partialOutput) {
    return new WorkerResult(partialOutput, new WorkerOutcome.Failed(reason));
}
public static WorkerResult expired(String reason, Map<String, Object> partialOutput) {
    return new WorkerResult(partialOutput, new WorkerOutcome.Expired(reason));
}
```

Parameter order `(reason, partialOutput)` — reason first for consistency with existing no-output overloads.

## Testing

1. **PlannedAction** — construction, both factories, null rejection, null parameters → empty map
2. **WorkerOutcome.Success** — carries PlannedAction, carries null, factory overloads, `success(null)` rejected
3. **WorkerResult.of(output, action)** — creates Success with action, output preserved, `of(output, null)` rejected
4. **Partial-output overloads** — all three carry partial output and correct outcome type
5. **Existing tests** — all pass unchanged (`isInstanceOf(Success.class)` assertions unaffected)

## Blast Radius

**Within this repo:** 1 site — `WorkerOutcome.success()` factory internals. All existing tests unaffected.

**Downstream (engine#543, out of scope):**

- **Construction sites:** ~6 `new Success()` calls become `new Success(null)`.
- **Read sites (access pattern change):** Every `workerResult.plannedAction()` call becomes `outcome instanceof Success s ? s.plannedAction() : null`. Key sites: `QuartzWorkerExecutionJob.onSuccess()`, `WorkflowExecutionCompletedHandler.handleWithPlannedAction()`, `WorkflowExecutionCompletedHandler.handleGate()`.
- **Field rename:** Engine call sites using `description` and `context` map to `description` and `parameters` — `description` is unchanged, `context` → `parameters` at all `PlannedAction.of()` call sites.
- **Dead tests:** `WorkerResultExpiredTest.expired_outcome_rejects_planned_action()` becomes dead code — the runtime validation it tests is replaced by structural enforcement (the compiler prevents a PlannedAction on non-Success outcomes).
- **Build:** Engine must `mvn clean install` (not incremental) after consuming updated 0.2-SNAPSHOT (GE-20260526-43a51d).

## After Completion

Run `mvn clean install` so the updated 0.2-SNAPSHOT is in the local Maven repo. Engine#543 is blocked on this.
