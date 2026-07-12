package io.casehub.worker.api;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WorkerFunctionTest {

  @SuppressWarnings("unchecked")
  @Test
  void sync_is_workerFunction() {
    WorkerFunction<?> fn = new WorkerFunction.Sync<>(Map.class, input -> WorkerResult.of(Map.of()));
    assertThat(fn).isInstanceOf(WorkerFunction.class);
  }

  @SuppressWarnings("unchecked")
  @Test
  void sync_fn_accessor_returns_function() {
    var          sync   = new WorkerFunction.Sync<>(Map.class, (Map input) -> WorkerResult.of(Map.of("key", "value")));
    WorkerResult result = sync.fn().apply(Map.of());
    assertThat(result.output()).containsEntry("key", "value");
  }

    @Test
    void workerFunction_declares_inputType_method() throws Exception {
        assertThat(WorkerFunction.class.getDeclaredMethods())
                .extracting("name")
                .containsExactly("inputType");
    }

  @Test
  void typedSyncCarriesInputType() {
    var fn = new WorkerFunction.Sync<>(String.class, s -> WorkerResult.of(Map.of("len", s.length())));
    assertThat(fn.inputType()).isEqualTo(String.class);
  }

  @Test
  void untypedSyncDefaultsToMapClass() {
    var fn = new WorkerFunction.Sync<>(Map.class, input -> WorkerResult.of(Map.of()));
    assertThat(fn.inputType()).isEqualTo(Map.class);
  }

  @Test
  void noneHasVoidInputType() {
    assertThat(WorkerFunction.NONE.inputType()).isEqualTo(Void.class);
  }

  @Test
  void syncRejectsNullInputType() {
    org.assertj.core.api.Assertions.assertThatThrownBy(
               () -> new WorkerFunction.Sync<>(null, input -> WorkerResult.of(Map.of())))
                                   .isInstanceOf(NullPointerException.class);
  }

  @Test
  void syncRejectsNullFunction() {
    org.assertj.core.api.Assertions.assertThatThrownBy(
               () -> new WorkerFunction.Sync<>(String.class, null))
                                   .isInstanceOf(NullPointerException.class);
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
