package io.casehub.worker.api;

public interface DataChannel<T> extends AutoCloseable {
    void send(Exchange<T> exchange);
    Exchange<T> receive();
    boolean isClosed();
    @Override void close();
}
