package io.casehub.worker.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class WorkerFunctionPersistentTest {

    @Test
    void persistent_captures_input_type() {
        WorkerFunction.Persistent<Map> fn =
                new WorkerFunction.Persistent<>(Map.class, scope -> {});
        assertThat(fn.inputType()).isEqualTo(Map.class);
    }

    @Test
    void persistent_output_type_is_void() {
        WorkerFunction.Persistent<String> fn =
                new WorkerFunction.Persistent<>(String.class, scope -> {});
        assertThat(fn.outputType()).isEqualTo(Void.class);
    }

    @Test
    void persistent_is_worker_function() {
        WorkerFunction<String, Void> fn =
                new WorkerFunction.Persistent<>(String.class, scope -> {});
        assertThat(fn).isInstanceOf(WorkerFunction.class);
    }
}
