package io.casehub.worker.api;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

    @SuppressWarnings("unchecked")
    @Test
    void async_is_workerFunction() {
        WorkerFunction<?> fn = new WorkerFunction.Async<>(Map.class,
                                                          input -> java.util.concurrent.CompletableFuture.completedFuture(WorkerResult.of(Map.of())));
        assertThat(fn).isInstanceOf(WorkerFunction.class);
    }

  @SuppressWarnings("unchecked")
  @Test
  void async_fn_accessor_returns_function() {
    var async = new WorkerFunction.Async<>(Map.class,
                                           (Map input) -> java.util.concurrent.CompletableFuture.completedFuture(
                                                   WorkerResult.of(Map.of("key", "value"))));
    java.util.concurrent.CompletionStage<WorkerResult> stage  = async.fn().apply(Map.of());
    WorkerResult                                       result = stage.toCompletableFuture().join();
    assertThat(result.output()).containsEntry("key", "value");
  }

  @Test
  void asyncCarriesInputType() {
    var fn = new WorkerFunction.Async<>(String.class,
                                        s -> java.util.concurrent.CompletableFuture.completedFuture(WorkerResult.of(Map.of("len", s.length()))));
    assertThat(fn.inputType()).isEqualTo(String.class);
  }

  @Test
  void asyncRejectsNullInputType() {
    org.assertj.core.api.Assertions.assertThatThrownBy(
               () -> new WorkerFunction.Async<>(null,
                                                input -> java.util.concurrent.CompletableFuture.completedFuture(WorkerResult.of(Map.of()))))
                                   .isInstanceOf(NullPointerException.class);
  }

  @Test
  void asyncRejectsNullFunction() {
    org.assertj.core.api.Assertions.assertThatThrownBy(
               () -> new WorkerFunction.Async<>(String.class, null))
                                   .isInstanceOf(NullPointerException.class);
  }

  @Test
  void async_is_not_sync() {
    WorkerFunction<?> fn = new WorkerFunction.Async<>(Map.class,
                                                      input -> java.util.concurrent.CompletableFuture.completedFuture(WorkerResult.of(Map.of())));
    assertThat(fn).isNotInstanceOf(WorkerFunction.Sync.class);
  }

  @Test
  void builder_asyncFunction_createsAsyncWorker() {
    Worker worker = Worker.builder()
                          .name("async-w").capabilityName("cap")
                          .asyncFunction(input -> java.util.concurrent.CompletableFuture.completedFuture(
                                  WorkerResult.of(Map.of("done", true))))
                          .build();
    assertThat(worker.function()).isInstanceOf(WorkerFunction.Async.class);
  }

  @Test
  void builder_typedAsyncFunction_createsAsyncWorker() {
    Worker worker = Worker.builder()
                          .name("typed-async").capabilityName("cap")
                          .<String>fn().applyAsync(s -> java.util.concurrent.CompletableFuture.completedFuture(
                    WorkerResult.of(Map.of("len", s.length()))))
                          .build();
    assertThat(worker.function()).isInstanceOf(WorkerFunction.Async.class);
    assertThat(worker.function().inputType()).isEqualTo(String.class);
  }


}
