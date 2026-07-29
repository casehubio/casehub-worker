package io.casehub.worker.api;

public class ScopeTerminatedException extends RuntimeException {
    public ScopeTerminatedException() {
        super("Scope terminated — engine-initiated shutdown");
    }
}
