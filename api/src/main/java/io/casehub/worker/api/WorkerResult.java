package io.casehub.worker.api;

import java.util.Map;

public record WorkerResult(Map<String, Object> output, WorkerOutcome outcome) {
    public static WorkerResult of(Map<String, Object> output) {
        return new WorkerResult(output, WorkerOutcome.success());
    }
    public static WorkerResult declined(String reason) {
        return new WorkerResult(Map.of(), new WorkerOutcome.Declined(reason));
    }
    public static WorkerResult failed(String reason) {
        return new WorkerResult(Map.of(), new WorkerOutcome.Failed(reason));
    }
    public static WorkerResult expired(String reason) {
        return new WorkerResult(Map.of(), new WorkerOutcome.Expired(reason));
    }
}
