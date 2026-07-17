package io.casehub.worker.api;

import java.util.Objects;
import java.util.function.Function;

public interface WorkerFunction<T> {

    WorkerFunction<Void> NONE = new None();

    Class<T> inputType();

    record Sync<T>(Class<T> inputType, Function<T, WorkerResult> fn) implements WorkerFunction<T> {
        public Sync {
            Objects.requireNonNull(inputType, "inputType must not be null");
            Objects.requireNonNull(fn, "fn must not be null");
        }
    }

    record Async<T>(Class<T> inputType,
                    java.util.function.Function<T, java.util.concurrent.CompletionStage<WorkerResult>> fn) implements WorkerFunction<T> {
        public Async {
            Objects.requireNonNull(inputType, "inputType must not be null");
            Objects.requireNonNull(fn, "fn must not be null");
        }
    }


    record None() implements WorkerFunction<Void> {
        @Override
        public Class<Void> inputType() { return Void.class; }
    }
}
