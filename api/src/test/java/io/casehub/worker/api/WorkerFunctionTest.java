package io.casehub.worker.api;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WorkerFunctionTest {

  @Test
  void sync_is_workerFunction() {
    WorkerFunction fn = new WorkerFunction.Sync(input -> WorkerResult.of(Map.of()));
    assertThat(fn).isInstanceOf(WorkerFunction.class);
  }

  @Test
  void sync_fn_accessor_returns_function() {
    var sync = new WorkerFunction.Sync(input -> WorkerResult.of(Map.of("key", "value")));
    WorkerResult result = sync.fn().apply(Map.of());
    assertThat(result.output()).containsEntry("key", "value");
  }

  @Test
  void workerFunction_has_no_execute_method() throws Exception {
    assertThat(WorkerFunction.class.getDeclaredMethods()).isEmpty();
  }

  @Test
  void none_is_workerFunction() {
    assertThat(WorkerFunction.NONE).isInstanceOf(WorkerFunction.class);
  }

  @Test
  void none_is_not_sync() {
    assertThat(WorkerFunction.NONE).isNotInstanceOf(WorkerFunction.Sync.class);
  }

  @Test
  void none_singleton_equals_new_instance() {
    assertThat(WorkerFunction.NONE).isEqualTo(new WorkerFunction.None());
  }

  @Test
  void none_is_same_reference_across_accesses() {
    assertThat(WorkerFunction.NONE).isSameAs(WorkerFunction.NONE);
  }
}
