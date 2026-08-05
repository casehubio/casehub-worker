# io.casehub.worker.api.WorkerFunction.Sync

**Package:** `io.casehub.worker.api`

**Kind:** `record`

## Fields

### `fn` (`java.util.function.BiFunction<T,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<R>>`)

### `inputType` (`java.lang.Class<T>`)

### `outputType` (`java.lang.Class<R>`)

## Record Components

### `fn` (`java.util.function.BiFunction<T,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<R>>`)

### `inputType` (`java.lang.Class<T>`)

### `outputType` (`java.lang.Class<R>`)

## Constructors

### `public Sync(java.lang.Class<T> inputType, java.lang.Class<R> outputType, java.util.function.BiFunction<T,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<R>> fn)`

#### Parameters

- `inputType` (`java.lang.Class<T>`)
- `outputType` (`java.lang.Class<R>`)
- `fn` (`java.util.function.BiFunction<T,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<R>>`)

## Methods

### `public final boolean equals(java.lang.Object o)`

#### Parameters

- `o` (`java.lang.Object`)

### `public java.util.function.BiFunction<T,io.casehub.worker.api.WorkerScope,io.casehub.worker.api.WorkerResult<R>> fn()`

### `public final int hashCode()`

### `public java.lang.Class<T> inputType()`

### `public java.lang.Class<R> outputType()`

### `public final java.lang.String toString()`
