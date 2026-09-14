# io.casehub.worker.api.Exchange

**Package:** `io.casehub.worker.api`

**Kind:** `record`

## Fields

### `body` (`T`)

### `headers` (`java.util.Map<java.lang.String,java.lang.Object>`)

### `properties` (`java.util.Map<java.lang.String,java.lang.Object>`)

## Record Components

### `body` (`T`)

### `headers` (`java.util.Map<java.lang.String,java.lang.Object>`)

### `properties` (`java.util.Map<java.lang.String,java.lang.Object>`)

## Constructors

### `public Exchange(T body, java.util.Map<java.lang.String,java.lang.Object> headers, java.util.Map<java.lang.String,java.lang.Object> properties)`

#### Parameters

- `body` (`T`)
- `headers` (`java.util.Map<java.lang.String,java.lang.Object>`)
- `properties` (`java.util.Map<java.lang.String,java.lang.Object>`)

## Methods

### `public T body()`

### `public final boolean equals(java.lang.Object o)`

#### Parameters

- `o` (`java.lang.Object`)

### `public final int hashCode()`

### `public V header(java.lang.String key)`

#### Parameters

- `key` (`java.lang.String`)

### `public V header(java.lang.String key, V defaultValue)`

#### Parameters

- `key` (`java.lang.String`)
- `defaultValue` (`V`)

### `public java.util.Map<java.lang.String,java.lang.Object> headers()`

### `public static io.casehub.worker.api.Exchange<T> of(T body)`

#### Parameters

- `body` (`T`)

### `public static io.casehub.worker.api.Exchange<T> of(T body, java.util.Map<java.lang.String,java.lang.Object> headers)`

#### Parameters

- `body` (`T`)
- `headers` (`java.util.Map<java.lang.String,java.lang.Object>`)

### `public java.util.Map<java.lang.String,java.lang.Object> properties()`

### `public V property(java.lang.String key)`

#### Parameters

- `key` (`java.lang.String`)

### `public final java.lang.String toString()`

### `public io.casehub.worker.api.Exchange<U> withBody(U newBody)`

#### Parameters

- `newBody` (`U`)

### `public io.casehub.worker.api.Exchange<T> withHeader(java.lang.String key, java.lang.Object value)`

#### Parameters

- `key` (`java.lang.String`)
- `value` (`java.lang.Object`)

### `public io.casehub.worker.api.Exchange<T> withHeaders(java.util.Map<java.lang.String,java.lang.Object> newHeaders)`

#### Parameters

- `newHeaders` (`java.util.Map<java.lang.String,java.lang.Object>`)

### `public io.casehub.worker.api.Exchange<T> withProperty(java.lang.String key, java.lang.Object value)`

#### Parameters

- `key` (`java.lang.String`)
- `value` (`java.lang.Object`)

### `public io.casehub.worker.api.Exchange<T> withoutHeader(java.lang.String key)`

#### Parameters

- `key` (`java.lang.String`)
