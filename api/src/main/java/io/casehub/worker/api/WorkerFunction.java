package io.casehub.worker.api;

public interface WorkerFunction<T, R> {

    WorkerFunction<Void, Void> NONE = new None();

    Class<T> inputType();

    Class<R> outputType();

    record Sync<T, R>(Class<T> inputType, Class<R> outputType,
                      java.util.function.BiFunction<T, WorkerScope, WorkerResult<R>> fn)
            implements WorkerFunction<T, R> {
        public Sync {
            java.util.Objects.requireNonNull(inputType, "inputType must not be null");
            java.util.Objects.requireNonNull(outputType, "outputType must not be null");
            java.util.Objects.requireNonNull(fn, "fn must not be null");
        }
    }

    record None() implements WorkerFunction<Void, Void> {
        @Override
        public Class<Void> inputType()  {return Void.class;}

        @Override
        public Class<Void> outputType() {return Void.class;}
    }

    record Persistent<T>(Class<T> inputType,
                         java.util.function.Consumer<PersistentScope<T>> handler)
            implements WorkerFunction<T, Void> {
        public Persistent {
            java.util.Objects.requireNonNull(inputType, "inputType must not be null");
            java.util.Objects.requireNonNull(handler, "handler must not be null");
        }

        @Override
        public Class<Void> outputType() {
            return Void.class;
        }
    }

    record ExchangeProcessor<T, R>(
            Class<T> bodyInputType,
            Class<R> bodyOutputType,
            java.util.function.BiFunction<Exchange<T>, WorkerScope, WorkerResult<Exchange<R>>> fn
    ) implements ExchangeAwareFunction<T, R> {

        public ExchangeProcessor {
            java.util.Objects.requireNonNull(bodyInputType, "bodyInputType must not be null");
            java.util.Objects.requireNonNull(bodyOutputType, "bodyOutputType must not be null");
            java.util.Objects.requireNonNull(fn, "fn must not be null");
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<Exchange<T>> inputType() {return (Class) Exchange.class;}

        @Override
        @SuppressWarnings("unchecked")
        public Class<Exchange<R>> outputType() {return (Class) Exchange.class;}

        @SuppressWarnings({"unchecked", "rawtypes"})
        public <S> ExchangeProcessor<T, S> andThen(ExchangeProcessor<R, S> next) {
            return new ExchangeProcessor<>(bodyInputType, next.bodyOutputType,
                                           (exchange, scope) -> {
                                               WorkerResult<Exchange<R>> first = fn.apply(exchange, scope);
                                               if (!(first.outcome() instanceof WorkerOutcome.Success)) {
                                                   return (WorkerResult) first;
                                               }
                                               return next.fn().apply(first.output(), scope);
                                           });
        }

    }


}
