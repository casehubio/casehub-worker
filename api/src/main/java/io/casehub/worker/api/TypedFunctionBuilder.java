package io.casehub.worker.api;

import java.util.function.Function;

public class TypedFunctionBuilder<T> {
    private final Worker.Builder parent;
    private final Class<?> runtimeType;

    TypedFunctionBuilder(Worker.Builder parent, Class<?> runtimeType) {
        this.parent = parent;
        this.runtimeType = runtimeType;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Worker.Builder apply(Function<T, WorkerResult> fn) {
        parent.setFunction(new WorkerFunction.Sync(runtimeType, fn));
        return parent;
    }
}
