# io.casehub.worker.api.WorkerFunction.ExchangeProcessor

**Package:** `io.casehub.worker.api`

**Kind:** `record`

## Fields

### `bodyInputType` (`java.lang.Class<T>`)

### `bodyOutputType` (`java.lang.Class<R>`)

### `fn` (`java.util.function.BiFunction<io.casehub.worker.api.Exchange<T>,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<io.casehub.worker.api.Exchange<R>>>`)

## Record Components

### `bodyInputType` (`java.lang.Class<T>`)

### `bodyOutputType` (`java.lang.Class<R>`)

### `fn` (`java.util.function.BiFunction<io.casehub.worker.api.Exchange<T>,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<io.casehub.worker.api.Exchange<R>>>`)

## Constructors

### `public ExchangeProcessor(java.lang.Class<T> bodyInputType, java.lang.Class<R> bodyOutputType, java.util.function.BiFunction<io.casehub.worker.api.Exchange<T>,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<io.casehub.worker.api.Exchange<R>>> fn)`

#### Parameters

- `bodyInputType` (`java.lang.Class<T>`)
- `bodyOutputType` (`java.lang.Class<R>`)
- `fn` (`java.util.function.BiFunction<io.casehub.worker.api.Exchange<T>,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<io.casehub.worker.api.Exchange<R>>>`)

## Methods

### `public io.casehub.worker.api.WorkerFunction.ExchangeProcessor<T,S> andThen(io.casehub.worker.api.WorkerFunction.ExchangeProcessor<R,S> next)`

#### Parameters

- `next` (`io.casehub.worker.api.WorkerFunction.ExchangeProcessor<R,S>`)

### `public java.lang.Class<T> bodyInputType()`

### `public java.lang.Class<R> bodyOutputType()`

### `public final boolean equals(java.lang.Object o)`

#### Parameters

- `o` (`java.lang.Object`)

### `public java.util.function.BiFunction<io.casehub.worker.api.Exchange<T>,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<io.casehub.worker.api.Exchange<R>>> fn()`

### `public final int hashCode()`

### `public java.lang.Class<io.casehub.worker.api.Exchange<T>> inputType()`

### `public java.lang.Class<io.casehub.worker.api.Exchange<R>> outputType()`

### `public final java.lang.String toString()`
