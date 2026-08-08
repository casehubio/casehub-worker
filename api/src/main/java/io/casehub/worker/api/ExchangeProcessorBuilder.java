package io.casehub.worker.api;

import java.util.Map;
import java.util.function.BiFunction;

public class ExchangeProcessorBuilder<T> {
    private final Worker.Builder parent;
    private final Class<?> runtimeType;

    ExchangeProcessorBuilder(Worker.Builder parent, Class<?> runtimeType) {
        this.parent = parent;
        this.runtimeType = runtimeType;
    }

    public <R> ExchangeProcessorOutputBuilder<T, R> returning(Class<R> outputType) {
        return new ExchangeProcessorOutputBuilder<>(parent, runtimeType, outputType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Worker.Builder apply(BiFunction<Exchange<T>, WorkerScope, WorkerResult<Exchange<Map<String, Object>>>> fn) {
        parent.setFunction(new WorkerFunction.ExchangeProcessor(runtimeType, Map.class, fn));
        return parent;
    }

    public static class ExchangeProcessorOutputBuilder<T, R> {
        private final Worker.Builder parent;
        private final Class<?> runtimeInputType;
        private final Class<R> outputType;

        ExchangeProcessorOutputBuilder(Worker.Builder parent, Class<?> runtimeInputType, Class<R> outputType) {
            this.parent = parent;
            this.runtimeInputType = runtimeInputType;
            this.outputType = outputType;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public Worker.Builder apply(BiFunction<Exchange<T>, WorkerScope, WorkerResult<Exchange<R>>> fn) {
            parent.setFunction(new WorkerFunction.ExchangeProcessor(runtimeInputType, outputType, fn));
            return parent;
        }
    }
}
