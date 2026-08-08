package io.casehub.worker.api;

public interface ExchangeAwareFunction<T, R> extends WorkerFunction<Exchange<T>, Exchange<R>> {
    Class<T> bodyInputType();
    Class<R> bodyOutputType();
}
