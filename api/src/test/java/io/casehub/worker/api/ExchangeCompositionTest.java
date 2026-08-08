package io.casehub.worker.api;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExchangeCompositionTest {

    private static final WorkerScope NULL_SCOPE = null;

    @Test
    void andThen_chainsBodyThroughBothSteps() {
        var toUpper = new WorkerFunction.ExchangeProcessor<>(
                String.class, String.class,
                (ex, scope) -> WorkerResult.of(ex.withBody(ex.body().toUpperCase())));

        var addExclaim = new WorkerFunction.ExchangeProcessor<>(
                String.class, String.class,
                (ex, scope) -> WorkerResult.of(ex.withBody(ex.body() + "!")));

        var composed = toUpper.andThen(addExclaim);
        var result = composed.fn().apply(Exchange.of("hello"), NULL_SCOPE);

        assertThat(result.output().body()).isEqualTo("HELLO!");
    }

    @Test
    void andThen_mergesHeaders() {
        var step1 = new WorkerFunction.ExchangeProcessor<>(
                String.class, String.class,
                (ex, scope) -> WorkerResult.of(ex.withHeader("step", "1")));

        var step2 = new WorkerFunction.ExchangeProcessor<>(
                String.class, String.class,
                (ex, scope) -> WorkerResult.of(ex.withHeader("step2", "2")));

        var composed = step1.andThen(step2);
        var result = composed.fn().apply(Exchange.of("body", Map.of("original", "yes")), NULL_SCOPE);

        assertThat(result.output().headers())
                .containsEntry("original", "yes")
                .containsEntry("step", "1")
                .containsEntry("step2", "2");
    }

    @Test
    void andThen_mergesProperties() {
        var step1 = new WorkerFunction.ExchangeProcessor<>(
                String.class, String.class,
                (ex, scope) -> WorkerResult.of(ex.withProperty("p1", "v1")));

        var step2 = new WorkerFunction.ExchangeProcessor<>(
                String.class, String.class,
                (ex, scope) -> WorkerResult.of(ex.withProperty("p2", "v2")));

        var composed = step1.andThen(step2);
        var result = composed.fn().apply(Exchange.of("body"), NULL_SCOPE);

        assertThat(result.output().properties())
                .containsEntry("p1", "v1")
                .containsEntry("p2", "v2");
    }

    @Test
    void andThen_shortCircuitsOnFailure() {
        var step1 = new WorkerFunction.ExchangeProcessor<>(
                String.class, String.class,
                (ex, scope) -> WorkerResult.declined("nope"));

        var step2 = new WorkerFunction.ExchangeProcessor<>(
                String.class, String.class,
                (ex, scope) -> {
                    throw new AssertionError("should not be called");
                });

        var composed = step1.andThen(step2);
        var result = composed.fn().apply(Exchange.of("hello"), NULL_SCOPE);

        assertThat(result.outcome()).isInstanceOf(WorkerOutcome.Declined.class);
    }

    @Test
    void andThen_preservesBodyTypes() {
        var step1 = new WorkerFunction.ExchangeProcessor<>(
                String.class, Integer.class,
                (ex, scope) -> WorkerResult.of(ex.withBody(ex.body().length())));

        var step2 = new WorkerFunction.ExchangeProcessor<>(
                Integer.class, Boolean.class,
                (ex, scope) -> WorkerResult.of(ex.withBody(ex.body() > 3)));

        var composed = step1.andThen(step2);

        assertThat(composed.bodyInputType()).isEqualTo(String.class);
        assertThat(composed.bodyOutputType()).isEqualTo(Boolean.class);

        var result = composed.fn().apply(Exchange.of("hello"), NULL_SCOPE);
        assertThat(result.output().body()).isTrue();
    }
}
