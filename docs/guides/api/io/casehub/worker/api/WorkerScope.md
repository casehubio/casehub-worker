# io.casehub.worker.api.WorkerScope

**Package:** `io.casehub.worker.api`

**Kind:** `interface`

Minimal execution scope passed to worker functions as an explicit parameter.

<p>Replaces ThreadLocal-based `WorkerExecutionContext`. References only worker-api types,
avoiding circular dependencies with engine-api. `WorkerRuntime` (in engine-api) extends
this interface with engine-specific methods (`context()`, `spawnCase()`).

## Methods

### `public default java.util.Map<java.lang.String,java.lang.Object> accumulatedState()`

### `public abstract java.util.UUID caseId()`

### `public abstract io.casehub.worker.api.WorkerResult<R> execute(io.casehub.worker.api.WorkerFunction<T,R> function, T input)`

#### Parameters

- `function` (`io.casehub.worker.api.WorkerFunction<T,R>`)
- `input` (`T`)

### `public abstract io.casehub.worker.api.WorkerResult<?> execute(java.lang.String workerName, java.util.Map<java.lang.String,java.lang.Object> input)`

#### Parameters

- `workerName` (`java.lang.String`)
- `input` (`java.util.Map<java.lang.String,java.lang.Object>`)

### `public abstract java.lang.String taskId()`
