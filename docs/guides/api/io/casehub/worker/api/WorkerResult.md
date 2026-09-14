# io.casehub.worker.api.WorkerResult

**Package:** `io.casehub.worker.api`

**Kind:** `record`

## Fields

### `outcome` (`io.casehub.worker.api.WorkerOutcome<R>`)

### `output` (`R`)

### `reasoning` (`java.lang.String`)

## Record Components

### `outcome` (`io.casehub.worker.api.WorkerOutcome<R>`)

### `output` (`R`)

### `reasoning` (`java.lang.String`)

## Constructors

### `public WorkerResult(R output, io.casehub.worker.api.WorkerOutcome<R> outcome)`

#### Parameters

- `output` (`R`)
- `outcome` (`io.casehub.worker.api.WorkerOutcome<R>`)

### `public WorkerResult(R output, io.casehub.worker.api.WorkerOutcome<R> outcome, java.lang.String reasoning)`

#### Parameters

- `output` (`R`)
- `outcome` (`io.casehub.worker.api.WorkerOutcome<R>`)
- `reasoning` (`java.lang.String`)

## Methods

### `public static io.casehub.worker.api.WorkerResult<R> completed(R output)`

#### Parameters

- `output` (`R`)

### `public static io.casehub.worker.api.WorkerResult<R> declined(java.lang.String reason)`

#### Parameters

- `reason` (`java.lang.String`)

### `public static io.casehub.worker.api.WorkerResult<R> declined(java.lang.String reason, R partialOutput)`

#### Parameters

- `reason` (`java.lang.String`)
- `partialOutput` (`R`)

### `public final boolean equals(java.lang.Object o)`

#### Parameters

- `o` (`java.lang.Object`)

### `public static io.casehub.worker.api.WorkerResult<R> expired(java.lang.String reason)`

#### Parameters

- `reason` (`java.lang.String`)

### `public static io.casehub.worker.api.WorkerResult<R> expired(java.lang.String reason, R partialOutput)`

#### Parameters

- `reason` (`java.lang.String`)
- `partialOutput` (`R`)

### `public static io.casehub.worker.api.WorkerResult<R> failed(java.lang.String reason)`

#### Parameters

- `reason` (`java.lang.String`)

### `public static io.casehub.worker.api.WorkerResult<R> failed(java.lang.String reason, R partialOutput)`

#### Parameters

- `reason` (`java.lang.String`)
- `partialOutput` (`R`)

### `public final int hashCode()`

### `public static io.casehub.worker.api.WorkerResult<R> of(R output)`

#### Parameters

- `output` (`R`)

### `public static io.casehub.worker.api.WorkerResult<R> of(R output, io.casehub.worker.api.PlannedAction action)`

#### Parameters

- `output` (`R`)
- `action` (`io.casehub.worker.api.PlannedAction`)

### `public io.casehub.worker.api.WorkerOutcome<R> outcome()`

### `public R output()`

### `public java.lang.String reasoning()`

### `public final java.lang.String toString()`

### `public io.casehub.worker.api.WorkerResult<R> withReasoning(java.lang.String reasoning)`

#### Parameters

- `reasoning` (`java.lang.String`)
