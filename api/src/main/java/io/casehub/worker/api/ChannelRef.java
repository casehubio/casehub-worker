package io.casehub.worker.api;

import java.io.Serializable;
import java.util.Objects;

public record ChannelRef<T>(String name, Class<T> recordType) implements Serializable {
    public ChannelRef {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(recordType, "recordType must not be null");
    }

    public static <T> ChannelRef<T> of(String name, Class<T> type) {
        return new ChannelRef<>(name, type);
    }
}
