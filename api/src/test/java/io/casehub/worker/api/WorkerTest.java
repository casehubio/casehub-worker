package io.casehub.worker.api;

import io.casehub.platform.api.governance.BackoffStrategy;
import io.casehub.platform.api.governance.ExecutionPolicy;
import io.casehub.platform.api.governance.RetryPolicy;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WorkerTest {

    @Test
    void syncWorker_executesFunction() {
        Worker worker = Worker.builder()
            .name("test-worker")
            .capability(Capability.of("process", "{}", "{}"))
            .function((WorkerFunction) input -> WorkerResult.of(Map.of("result", "done")))
            .build();

        assertThat(worker.name()).isEqualTo("test-worker");
        assertThat(worker.capabilities()).hasSize(1);
        assertThat(worker.capabilities().get(0).name()).isEqualTo("process");

        WorkerResult result = worker.function().execute(Map.of());
        assertThat(result.outcome()).isInstanceOf(WorkerOutcome.Success.class);
        assertThat(result.output()).containsEntry("result", "done");
    }

    @Test
    void worker_defaultExecutionPolicy() {
        Worker worker = Worker.builder()
            .name("default-policy")
            .capability(Capability.of("test", "{}", "{}"))
            .function((WorkerFunction) input -> WorkerResult.of(Map.of()))
            .build();

        assertThat(worker.executionPolicy()).isNotNull();
        assertThat(worker.executionPolicy().retries().maxAttempts()).isEqualTo(3);
    }

    @Test
    void worker_customExecutionPolicy() {
        ExecutionPolicy policy = new ExecutionPolicy(5000,
            new RetryPolicy(5, 500, BackoffStrategy.EXPONENTIAL));

        Worker worker = Worker.builder()
            .name("custom-policy")
            .capability(Capability.of("test", "{}", "{}"))
            .function((WorkerFunction) input -> WorkerResult.of(Map.of()))
            .executionPolicy(policy)
            .build();

        assertThat(worker.executionPolicy().timeoutMs()).isEqualTo(5000);
        assertThat(worker.executionPolicy().retries().maxAttempts()).isEqualTo(5);
    }

    @Test
    void workerResult_factoryMethods() {
        WorkerResult success = WorkerResult.of(Map.of("key", "value"));
        assertThat(success.outcome()).isInstanceOf(WorkerOutcome.Success.class);

        WorkerResult declined = WorkerResult.declined("not my job");
        assertThat(declined.outcome()).isInstanceOf(WorkerOutcome.Declined.class);

        WorkerResult failed = WorkerResult.failed("broken");
        assertThat(failed.outcome()).isInstanceOf(WorkerOutcome.Failed.class);

        WorkerResult expired = WorkerResult.expired("too slow");
        assertThat(expired.outcome()).isInstanceOf(WorkerOutcome.Expired.class);
    }

    @Test
    void capability_withDescription() {
        Capability cap = Capability.builder()
            .name("analyse")
            .inputSchema("{\"type\":\"object\"}")
            .outputSchema("{\"type\":\"object\"}")
            .description("Analyses input data")
            .build();

        assertThat(cap.name()).isEqualTo("analyse");
        assertThat(cap.description()).isEqualTo("Analyses input data");
    }
}
