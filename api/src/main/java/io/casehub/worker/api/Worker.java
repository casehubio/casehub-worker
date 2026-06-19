package io.casehub.worker.api;

import io.casehub.platform.api.governance.ExecutionPolicy;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record Worker(String name, List<Capability> capabilities, WorkerFunction function,
                     ExecutionPolicy executionPolicy, String description) {
    public Worker {
        Objects.requireNonNull(name);
        Objects.requireNonNull(capabilities);
        Objects.requireNonNull(function);
        if (executionPolicy == null) executionPolicy = new ExecutionPolicy();
        capabilities = List.copyOf(capabilities);
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private List<Capability> capabilities;
        private WorkerFunction function;
        private ExecutionPolicy executionPolicy;
        private String description;

        public Builder name(String n) { this.name = n; return this; }
        public Builder capabilities(Capability... c) { this.capabilities = Arrays.asList(c); return this; }
        public Builder capabilities(List<Capability> c) { this.capabilities = c; return this; }
        public Builder capability(Capability c) { this.capabilities = List.of(c); return this; }
        public Builder function(WorkerFunction f) { this.function = f; return this; }
        public Builder function(java.util.function.Function<java.util.Map<String, Object>, WorkerResult> fn) {
            this.function = new WorkerFunction.Sync(fn);
            return this;
        }
        public Builder executionPolicy(ExecutionPolicy p) { this.executionPolicy = p; return this; }
        public Builder description(String d) { this.description = d; return this; }

        public Worker build() {
            return new Worker(name, capabilities, function, executionPolicy, description);
        }
    }
}
