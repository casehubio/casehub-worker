package io.casehub.worker.testing;

import io.casehub.worker.api.Worker;
import io.casehub.worker.api.WorkerResult;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MockWorkerExecutor {
    private final AtomicInteger executionCount = new AtomicInteger(0);
    private final AtomicReference<String> lastWorkerName = new AtomicReference<>();

    public WorkerResult execute(Worker worker, Map<String, Object> input) {
        executionCount.incrementAndGet();
        lastWorkerName.set(worker.name());
        return worker.function().execute(input);
    }

    public int executionCount() { return executionCount.get(); }
    public String lastWorkerName() { return lastWorkerName.get(); }
    public void reset() { executionCount.set(0); lastWorkerName.set(null); }
}
