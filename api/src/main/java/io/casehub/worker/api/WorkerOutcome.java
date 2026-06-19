package io.casehub.worker.api;

public sealed interface WorkerOutcome {
    static WorkerOutcome success() { return new Success(); }
    record Success() implements WorkerOutcome {}
    record Declined(String reason) implements WorkerOutcome {}
    record Failed(String reason) implements WorkerOutcome {}
    record Expired(String reason) implements WorkerOutcome {}
}
