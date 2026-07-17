package io.casehub.worker.testing;

import io.casehub.worker.api.Capability;
import io.casehub.worker.api.Worker;
import io.casehub.worker.api.WorkerResult;

import java.util.Map;
import java.util.function.Function;

public final class TestWorkerBuilder {
    private TestWorkerBuilder() {}

    public record WorkerWithCapability(Worker worker, Capability capability) {}

    public static Worker sync(String name, Function<Map<String, Object>, WorkerResult> fn) {
        return Worker.builder()
            .name(name)
            .capabilityName(name)
            .function(fn)
            .build();
    }

    public static WorkerWithCapability syncWithCapability(String name,
            Function<Map<String, Object>, WorkerResult> fn) {
        Worker worker = Worker.builder()
            .name(name)
            .capabilityName(name)
            .function(fn)
            .build();
        Capability capability = Capability.of(name, "{}", "{}");
        return new WorkerWithCapability(worker, capability);
    }

    public static WorkerWithCapability syncWithCapability(String name,
            String inputSchema, String outputSchema,
            Function<Map<String, Object>, WorkerResult> fn) {
        Worker worker = Worker.builder()
            .name(name)
            .capabilityName(name)
            .function(fn)
            .build();
        Capability capability = Capability.of(name, inputSchema, outputSchema);
        return new WorkerWithCapability(worker, capability);
    }

    public static Worker async(String name,
                               Function<Map<String, Object>, java.util.concurrent.CompletionStage<WorkerResult>> fn) {
        return Worker.builder()
                     .name(name).capabilityName(name)
                     .asyncFunction(fn)
                     .build();
    }

    public static WorkerWithCapability asyncWithCapability(String name,
                                                           Function<Map<String, Object>, java.util.concurrent.CompletionStage<WorkerResult>> fn) {
        Worker worker = Worker.builder()
                              .name(name).capabilityName(name)
                              .asyncFunction(fn)
                              .build();
        Capability capability = Capability.of(name, "{}", "{}");
        return new WorkerWithCapability(worker, capability);
    }

    public static WorkerWithCapability asyncWithCapability(String name,
                                                           String inputSchema, String outputSchema,
                                                           Function<Map<String, Object>, java.util.concurrent.CompletionStage<WorkerResult>> fn) {
        Worker worker = Worker.builder()
                              .name(name).capabilityName(name)
                              .asyncFunction(fn)
                              .build();
        Capability capability = Capability.of(name, inputSchema, outputSchema);
        return new WorkerWithCapability(worker, capability);
    }


}
