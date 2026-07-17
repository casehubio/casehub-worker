package io.casehub.worker.testing;

import io.casehub.worker.api.WorkerOutcome;
import io.casehub.worker.api.WorkerResult;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TestWorkerBuilderTest {

    @Test
    void syncWithCapability_createsMatchingPair() {
        var wc = TestWorkerBuilder.syncWithCapability("greet",
            input -> WorkerResult.of(Map.of("greeting", "hello")));

        assertThat(wc.worker().name()).isEqualTo("greet");
        assertThat(wc.worker().capabilityNames()).containsExactly("greet");
        assertThat(wc.capability().name()).isEqualTo("greet");
        assertThat(wc.capability().inputSchema()).isEqualTo("{}");
        assertThat(wc.capability().outputSchema()).isEqualTo("{}");
    }

    @Test
    void syncWithCapability_functionExecutes() {
        var wc = TestWorkerBuilder.syncWithCapability("echo",
            input -> WorkerResult.of(input));

        @SuppressWarnings("unchecked")
        var result = ((io.casehub.worker.api.WorkerFunction.Sync<Map<String, Object>>) wc.worker().function())
            .fn().apply(Map.of("key", "value"));
        assertThat(result.outcome()).isInstanceOf(WorkerOutcome.Success.class);
        assertThat(result.output()).containsEntry("key", "value");
    }

    @Test
    void async_createsWorkerWithAsyncFunction() {
        var worker = TestWorkerBuilder.async("fetcher",
                                             input -> java.util.concurrent.CompletableFuture.completedFuture(WorkerResult.of(Map.of("ok", true))));
        assertThat(worker.name()).isEqualTo("fetcher");
        assertThat(worker.function()).isInstanceOf(io.casehub.worker.api.WorkerFunction.Async.class);
    }

    @Test
    void asyncWithCapability_createsMatchingPair() {
        var wc = TestWorkerBuilder.asyncWithCapability("fetcher",
                                                       input -> java.util.concurrent.CompletableFuture.completedFuture(WorkerResult.of(Map.of("ok", true))));
        assertThat(wc.worker().name()).isEqualTo("fetcher");
        assertThat(wc.worker().function()).isInstanceOf(io.casehub.worker.api.WorkerFunction.Async.class);
        assertThat(wc.capability().name()).isEqualTo("fetcher");
        assertThat(wc.capability().inputSchema()).isEqualTo("{}");
        assertThat(wc.capability().outputSchema()).isEqualTo("{}");
    }

    @Test
    void asyncWithCapability_withSchemas_appliesSchemas() {
        var wc = TestWorkerBuilder.asyncWithCapability("fetcher",
                                                       "{\"type\":\"object\"}", "{\"type\":\"object\"}",
                                                       input -> java.util.concurrent.CompletableFuture.completedFuture(WorkerResult.of(Map.of())));
        assertThat(wc.capability().inputSchema()).isEqualTo("{\"type\":\"object\"}");
        assertThat(wc.capability().outputSchema()).isEqualTo("{\"type\":\"object\"}");
    }


}
