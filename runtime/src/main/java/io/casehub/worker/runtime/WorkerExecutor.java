package io.casehub.worker.runtime;

import io.casehub.worker.api.Capability;
import io.casehub.worker.api.Worker;
import io.casehub.worker.api.WorkerResult;
import io.smallrye.mutiny.Uni;

public interface WorkerExecutor {
    Uni<WorkerResult> execute(Worker worker, Capability capability, Object input);
}
