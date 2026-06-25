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
}
