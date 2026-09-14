# io.casehub.worker.api.ExchangeProcessorBuilder.ExchangeProcessorOutputBuilder

**Package:** `io.casehub.worker.api`

**Kind:** `class`

## Fields

### `outputType` (`java.lang.Class<R>`)

### `parent` (`io.casehub.worker.api.Worker.Builder`)

### `runtimeInputType` (`java.lang.Class<?>`)

## Constructors

### `ExchangeProcessorOutputBuilder(io.casehub.worker.api.Worker.Builder parent, java.lang.Class<?> runtimeInputType, java.lang.Class<R> outputType)`

#### Parameters

- `parent` (`io.casehub.worker.api.Worker.Builder`)
- `runtimeInputType` (`java.lang.Class<?>`)
- `outputType` (`java.lang.Class<R>`)

## Methods

### `public io.casehub.worker.api.Worker.Builder apply(java.util.function.BiFunction<io.casehub.worker.api.Exchange<T>,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<io.casehub.worker.api.Exchange<R>>> fn)`

#### Parameters

- `fn` (`java.util.function.BiFunction<io.casehub.worker.api.Exchange<T>,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<io.casehub.worker.api.Exchange<R>>>`)
