# io.casehub.worker.api.TypedOutputBuilder

**Package:** `io.casehub.worker.api`

**Kind:** `class`

## Fields

### `outputType` (`java.lang.Class<R>`)

### `parent` (`io.casehub.worker.api.Worker.Builder`)

### `runtimeInputType` (`java.lang.Class<?>`)

## Constructors

### `TypedOutputBuilder(io.casehub.worker.api.Worker.Builder parent, java.lang.Class<?> runtimeInputType, java.lang.Class<R> outputType)`

#### Parameters

- `parent` (`io.casehub.worker.api.Worker.Builder`)
- `runtimeInputType` (`java.lang.Class<?>`)
- `outputType` (`java.lang.Class<R>`)

## Methods

### `public io.casehub.worker.api.Worker.Builder apply(java.util.function.BiFunction<T,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<R>> fn)`

#### Parameters

- `fn` (`java.util.function.BiFunction<T,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<R>>`)

### `public io.casehub.worker.api.Worker.Builder apply(java.util.function.Function<T,io.casehub.worker.api.WorkerResult<R>> fn)`

#### Parameters

- `fn` (`java.util.function.Function<T,io.casehub.worker.api.WorkerResult<R>>`)
