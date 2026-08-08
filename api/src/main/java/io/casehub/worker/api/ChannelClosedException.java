package io.casehub.worker.api;

public class ChannelClosedException extends RuntimeException {
    public ChannelClosedException() {
        super("Channel is closed");
    }

    public ChannelClosedException(String channelName) {
        super("Channel '" + channelName + "' is closed");
    }
}
