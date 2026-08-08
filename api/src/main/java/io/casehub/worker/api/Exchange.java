package io.casehub.worker.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record Exchange<T>(
    T body,
    Map<String, Object> headers,
    Map<String, Object> properties
) {
    public Exchange {
        headers = headers == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        properties = properties == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(properties));
    }

    public static <T> Exchange<T> of(T body) {
        return new Exchange<>(body, Map.of(), Map.of());
    }

    public static <T> Exchange<T> of(T body, Map<String, Object> headers) {
        return new Exchange<>(body, headers, Map.of());
    }

    public <U> Exchange<U> withBody(U newBody) {
        return new Exchange<>(newBody, headers, properties);
    }

    public Exchange<T> withHeader(String key, Object value) {
        var h = new LinkedHashMap<>(headers);
        h.put(key, value);
        return new Exchange<>(body, h, properties);
    }

    public Exchange<T> withHeaders(Map<String, Object> newHeaders) {
        return new Exchange<>(body, newHeaders, properties);
    }

    public Exchange<T> withProperty(String key, Object value) {
        var p = new LinkedHashMap<>(properties);
        p.put(key, value);
        return new Exchange<>(body, headers, p);
    }

    public Exchange<T> withoutHeader(String key) {
        var h = new LinkedHashMap<>(headers);
        h.remove(key);
        return new Exchange<>(body, h, properties);
    }

    @SuppressWarnings("unchecked")
    public <V> V header(String key) {
        return (V) headers.get(key);
    }

    public <V> V header(String key, V defaultValue) {
        @SuppressWarnings("unchecked")
        V value = (V) headers.get(key);
        return value != null ? value : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <V> V property(String key) {
        return (V) properties.get(key);
    }
}
