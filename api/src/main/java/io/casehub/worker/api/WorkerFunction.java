package io.casehub.worker.api;

import java.util.Map;
import java.util.function.Function;

public interface WorkerFunction {
    WorkerResult execute(Map<String, Object> input);

    record Sync(Function<Map<String, Object>, WorkerResult> fn) implements WorkerFunction {
        @Override
        public WorkerResult execute(Map<String, Object> input) {
            return fn.apply(input);
        }
    }
}
