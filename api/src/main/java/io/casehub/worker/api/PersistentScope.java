package io.casehub.worker.api;

import java.util.Map;

public interface PersistentScope<T> extends WorkerScope {
    T nextEvent() throws ScopeTerminatedException;

    void emit(Map<String, Object> output);
}
