# io.casehub.worker.api.ExchangeProcessorBuilder

**Package:** `io.casehub.worker.api`

**Kind:** `class`

## Fields

### `parent` (`io.casehub.worker.api.Worker.Builder`)

### `runtimeType` (`java.lang.Class<?>`)

## Constructors

### `ExchangeProcessorBuilder(io.casehub.worker.api.Worker.Builder parent, java.lang.Class<?> runtimeType)`

#### Parameters

- `parent` (`io.casehub.worker.api.Worker.Builder`)
- `runtimeType` (`java.lang.Class<?>`)

## Methods

### `public io.casehub.worker.api.Worker.Builder apply(java.util.function.BiFunction<io.casehub.worker.api.Exchange<T>,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<io.casehub.worker.api.Exchange<java.util.Map<java.lang.String,java.lang.Object>>>> fn)`

#### Parameters

- `fn` (`java.util.function.BiFunction<io.casehub.worker.api.Exchange<T>,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<io.casehub.worker.api.Exchange<java.util.Map<java.lang.String,java.lang.Object>>>>`)

### `public io.casehub.worker.api.ExchangeProcessorBuilder.ExchangeProcessorOutputBuilder<T,R> returning(java.lang.Class<R> outputType)`

#### Parameters

- `outputType` (`java.lang.Class<R>`)
