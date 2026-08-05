# io.casehub.worker.api.PlannedAction

**Package:** `io.casehub.worker.api`

**Kind:** `record`

## Fields

### `actionType` (`java.lang.String`)

### `description` (`java.lang.String`)

### `parameters` (`java.util.Map<java.lang.String,java.lang.Object>`)

## Record Components

### `actionType` (`java.lang.String`)

### `description` (`java.lang.String`)

### `parameters` (`java.util.Map<java.lang.String,java.lang.Object>`)

## Constructors

### `public PlannedAction(java.lang.String description, java.lang.String actionType, java.util.Map<java.lang.String,java.lang.Object> parameters)`

#### Parameters

- `description` (`java.lang.String`)
- `actionType` (`java.lang.String`)
- `parameters` (`java.util.Map<java.lang.String,java.lang.Object>`)

## Methods

### `public java.lang.String actionType()`

### `public java.lang.String description()`

### `public final boolean equals(java.lang.Object o)`

#### Parameters

- `o` (`java.lang.Object`)

### `public final int hashCode()`

### `public static io.casehub.worker.api.PlannedAction of(java.lang.String description, java.lang.String actionType)`

#### Parameters

- `description` (`java.lang.String`)
- `actionType` (`java.lang.String`)

### `public static io.casehub.worker.api.PlannedAction of(java.lang.String description, java.lang.String actionType, java.util.Map<java.lang.String,java.lang.Object> parameters)`

#### Parameters

- `description` (`java.lang.String`)
- `actionType` (`java.lang.String`)
- `parameters` (`java.util.Map<java.lang.String,java.lang.Object>`)

### `public java.util.Map<java.lang.String,java.lang.Object> parameters()`

### `public final java.lang.String toString()`
