package io.casehub.worker.testing;

import io.casehub.worker.api.Worker;
import io.casehub.worker.api.WorkerResult;

import java.util.Map;
import java.util.function.Function;

public final class TestWorkerBuilder {
    private TestWorkerBuilder() {}

    public static Worker sync(String name, Function<Map<String, Object>, WorkerResult> fn) {
        return Worker.builder()
            .name(name)
            .capabilityName(name)
            .function(fn)
            .build();
    }
}
