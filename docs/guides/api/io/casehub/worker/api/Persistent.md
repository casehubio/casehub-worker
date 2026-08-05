# io.casehub.worker.api.WorkerFunction.Persistent

**Package:** `io.casehub.worker.api`

**Kind:** `record`

## Fields

### `handler` (`java.util.function.Consumer<io.casehub.worker.api.PersistentScope<T>>`)

### `inputType` (`java.lang.Class<T>`)

## Record Components

### `handler` (`java.util.function.Consumer<io.casehub.worker.api.PersistentScope<T>>`)

### `inputType` (`java.lang.Class<T>`)

## Constructors

### `public Persistent(java.lang.Class<T> inputType, java.util.function.Consumer<io.casehub.worker.api.PersistentScope<T>> handler)`

#### Parameters

- `inputType` (`java.lang.Class<T>`)
- `handler` (`java.util.function.Consumer<io.casehub.worker.api.PersistentScope<T>>`)

## Methods

### `public final boolean equals(java.lang.Object o)`

#### Parameters

- `o` (`java.lang.Object`)

### `public java.util.function.Consumer<io.casehub.worker.api.PersistentScope<T>> handler()`

### `public final int hashCode()`

### `public java.lang.Class<T> inputType()`

### `public java.lang.Class<java.lang.Void> outputType()`

### `public final java.lang.String toString()`
