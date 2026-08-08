package io.casehub.worker.api;

import java.util.Map;
import java.util.UUID;

/**
 * Minimal execution scope passed to worker functions as an explicit parameter.
 *
 * <p>Replaces ThreadLocal-based {@code WorkerExecutionContext}. References only worker-api types,
 * avoiding circular dependencies with engine-api. {@code WorkerRuntime} (in engine-api) extends
 * this interface with engine-specific methods ({@code context()}, {@code spawnCase()}).
 */
public interface WorkerScope {
    UUID caseId();
    String taskId();
    <T, R> WorkerResult<R> execute(WorkerFunction<T, R> function, T input);
    WorkerResult<?> execute(String workerName, Map<String, Object> input);

    default java.util.Map<String, Object> accumulatedState() {
        return java.util.Map.of();
    }

    default <T> DataChannel<T> channel(String name) {
        throw new UnsupportedOperationException("DataChannel requires engine context");
    }

    default <T> DataChannel<T> channel(ChannelRef<T> ref) {
        return channel(ref.name());
    }

    default <T> ChannelRef<T> createChannel(String name, Class<T> recordType) {
        throw new UnsupportedOperationException("DataChannel requires engine context");
    }


}
