package io.casehub.worker.api;

import java.util.Map;
import java.util.function.Function;

public interface WorkerFunction {

    WorkerFunction NONE = new None();

    record Sync(Function<Map<String, Object>, WorkerResult> fn) implements WorkerFunction {
    }

    record None() implements WorkerFunction {
    }
}
