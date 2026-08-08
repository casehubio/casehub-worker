package io.casehub.worker.api;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExchangeProcessorTest {

    @Test
    void constructorRejectsNullBodyInputType() {
        assertThatThrownBy(() -> new WorkerFunction.ExchangeProcessor<>(
                null, String.class, (ex, scope) -> WorkerResult.of(ex.withBody(""))))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructorRejectsNullBodyOutputType() {
        assertThatThrownBy(() -> new WorkerFunction.ExchangeProcessor<>(
                String.class, null, (ex, scope) -> WorkerResult.of(ex.withBody(""))))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructorRejectsNullFn() {
        assertThatThrownBy(() -> new WorkerFunction.ExchangeProcessor<String, String>(
                String.class, String.class, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void inputTypeReturnsExchangeClass() {
        var proc = new WorkerFunction.ExchangeProcessor<>(
                String.class, Integer.class, (ex, scope) -> WorkerResult.of(ex.withBody(0)));
        assertThat(proc.inputType()).isEqualTo(Exchange.class);
    }

    @Test
    void outputTypeReturnsExchangeClass() {
        var proc = new WorkerFunction.ExchangeProcessor<>(
                String.class, Integer.class, (ex, scope) -> WorkerResult.of(ex.withBody(0)));
        assertThat(proc.outputType()).isEqualTo(Exchange.class);
    }

    @Test
    void bodyInputTypeReturnsSpecifiedClass() {
        var proc = new WorkerFunction.ExchangeProcessor<>(
                String.class, Integer.class, (ex, scope) -> WorkerResult.of(ex.withBody(0)));
        assertThat(proc.bodyInputType()).isEqualTo(String.class);
    }

    @Test
    void bodyOutputTypeReturnsSpecifiedClass() {
        var proc = new WorkerFunction.ExchangeProcessor<>(
                String.class, Integer.class, (ex, scope) -> WorkerResult.of(ex.withBody(0)));
        assertThat(proc.bodyOutputType()).isEqualTo(Integer.class);
    }

    @Test
    void implementsExchangeAwareFunction() {
        var proc = new WorkerFunction.ExchangeProcessor<>(
                String.class, String.class, (ex, scope) -> WorkerResult.of(ex));
        assertThat(proc).isInstanceOf(ExchangeAwareFunction.class);
        assertThat(proc).isInstanceOf(WorkerFunction.class);
    }

    @Test
    void fnInvokesCorrectly() {
        var proc = new WorkerFunction.ExchangeProcessor<>(
                String.class, Integer.class,
                (ex, scope) -> WorkerResult.of(ex.withBody(ex.body().length())));
        Exchange<String> input = Exchange.of("hello", Map.of("src", "test"));
        WorkerResult<Exchange<Integer>> result = proc.fn().apply(input, null);
        assertThat(result.output().body()).isEqualTo(5);
        assertThat(result.output().headers()).containsEntry("src", "test");
    }

    @Test
    void builderCreatesExchangeProcessor() {
        @SuppressWarnings("unchecked")
        Worker worker = Worker.builder()
                              .name("test")
                              .capabilityName("cap")
                              .<Map<String, Object>>exchange()
                              .returning(Map.class)
                              .apply((ex, scope) -> WorkerResult.of(ex.withBody((Map) ex.body())))
                              .build();
        assertThat(worker.function()).isInstanceOf(WorkerFunction.ExchangeProcessor.class);
        assertThat(worker.function()).isInstanceOf(ExchangeAwareFunction.class);
        var proc = (ExchangeAwareFunction<?, ?>) worker.function();
        assertThat(proc.bodyInputType()).isEqualTo(Map.class);
        assertThat(proc.bodyOutputType()).isEqualTo(Map.class);}

    @SuppressWarnings("unchecked")
    @Test
    void convenienceExchangeBuilder() {
        Worker worker = Worker.builder()
                .name("test")
                .capabilityName("cap")
                .exchange((exchange, scope) -> WorkerResult.of(exchange.withBody(Map.of("done", true))))
                .build();
        assertThat(worker.function()).isInstanceOf(WorkerFunction.ExchangeProcessor.class);
        var proc = (WorkerFunction.ExchangeProcessor<Map<String, Object>, Map<String, Object>>) worker.function();
        assertThat(proc.bodyInputType()).isEqualTo(Map.class);
        assertThat(proc.bodyOutputType()).isEqualTo(Map.class);
    }
}
