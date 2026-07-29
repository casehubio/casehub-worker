package io.casehub.worker.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class WorkerResultCompletedTest {

    @Test
    void completed_factory_creates_completed_outcome() {
        WorkerResult<Map<String, Object>> result = WorkerResult.completed(Map.of("k", "v"));
        assertThat(result.outcome()).isInstanceOf(WorkerOutcome.Completed.class);
        assertThat(result.output()).containsEntry("k", "v");
    }

    @Test
    void completed_factory_with_null_output() {
        WorkerResult<Map<String, Object>> result = WorkerResult.completed(null);
        assertThat(result.outcome()).isInstanceOf(WorkerOutcome.Completed.class);
        assertThat(result.output()).isNull();
    }
}
