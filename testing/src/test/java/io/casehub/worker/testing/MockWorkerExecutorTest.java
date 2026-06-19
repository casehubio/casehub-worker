package io.casehub.worker.testing;

import io.casehub.worker.api.WorkerOutcome;
import io.casehub.worker.api.WorkerResult;
import io.casehub.worker.api.Worker;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MockWorkerExecutorTest {

    @Test
    void execute_bypassesPolicyEnforcement() {
        MockWorkerExecutor executor = new MockWorkerExecutor();
        Worker worker = TestWorkerBuilder.sync("test", input -> WorkerResult.of(Map.of("ok", true)));

        WorkerResult result = executor.execute(worker, Map.of());
        assertThat(result.outcome()).isInstanceOf(WorkerOutcome.Success.class);
        assertThat(executor.executionCount()).isEqualTo(1);
        assertThat(executor.lastWorkerName()).isEqualTo("test");
    }
}
