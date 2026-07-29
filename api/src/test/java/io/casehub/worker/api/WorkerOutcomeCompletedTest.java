package io.casehub.worker.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkerOutcomeCompletedTest {

    @Test
    void completed_is_a_worker_outcome() {
        WorkerOutcome<String> outcome = new WorkerOutcome.Completed<>();
        assertThat(outcome).isInstanceOf(WorkerOutcome.class);
    }

    @Test
    void completed_factory_method() {
        WorkerOutcome<String> outcome = WorkerOutcome.completed();
        assertThat(outcome).isInstanceOf(WorkerOutcome.Completed.class);
    }

    @Test
    void exhaustive_switch_compiles() {
        WorkerOutcome<String> outcome = WorkerOutcome.completed();
        String result =
                switch (outcome) {
                    case WorkerOutcome.Success<?> s -> "success";
                    case WorkerOutcome.Declined<?> d -> "declined";
                    case WorkerOutcome.Failed<?> f -> "failed";
                    case WorkerOutcome.Expired<?> e -> "expired";
                    case WorkerOutcome.Completed<?> c -> "completed";
                };
        assertThat(result).isEqualTo("completed");
    }
}
