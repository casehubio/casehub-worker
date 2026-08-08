package io.casehub.worker.api;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkerScopeChannelTest {

    private final WorkerScope scope = new WorkerScope() {
        @Override public UUID caseId() { return UUID.randomUUID(); }
        @Override public String taskId() { return "t"; }
        @Override public <T, R> WorkerResult<R> execute(WorkerFunction<T, R> f, T i) { return null; }
        @Override public WorkerResult<?> execute(String n, Map<String, Object> i) { return null; }
    };

    @Test
    void channelDefaultThrowsUnsupported() {
        assertThatThrownBy(() -> scope.channel("test"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("engine context");
    }

    @Test
    void createChannelDefaultThrowsUnsupported() {
        assertThatThrownBy(() -> scope.createChannel("test", String.class))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("engine context");
    }

    @Test
    void channelRefDelegatesToChannelName() {
        assertThatThrownBy(() -> scope.channel(ChannelRef.of("test", String.class)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
