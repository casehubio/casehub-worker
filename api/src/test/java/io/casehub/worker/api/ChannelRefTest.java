package io.casehub.worker.api;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChannelRefTest {

    @Test
    void ofCreatesRef() {
        ChannelRef<String> ref = ChannelRef.of("ch1", String.class);
        assertThat(ref.name()).isEqualTo("ch1");
        assertThat(ref.recordType()).isEqualTo(String.class);
    }

    @Test
    void constructorRejectsNullName() {
        assertThatThrownBy(() -> new ChannelRef<>(null, String.class))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructorRejectsNullType() {
        assertThatThrownBy(() -> new ChannelRef<>("ch", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void equality() {
        ChannelRef<String> a = ChannelRef.of("ch", String.class);
        ChannelRef<String> b = ChannelRef.of("ch", String.class);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void serializable() throws Exception {
        ChannelRef<String> original = ChannelRef.of("ch1", String.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(original);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            @SuppressWarnings("unchecked")
            ChannelRef<String> deserialized = (ChannelRef<String>) ois.readObject();
            assertThat(deserialized).isEqualTo(original);
        }
    }
}
