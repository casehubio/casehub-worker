package io.casehub.worker.api;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExchangeTest {

    @Test
    void ofBody_createsWithEmptyHeadersAndProperties() {
        Exchange<String> ex = Exchange.of("hello");
        assertThat(ex.body()).isEqualTo("hello");
        assertThat(ex.headers()).isEmpty();
        assertThat(ex.properties()).isEmpty();
    }

    @Test
    void ofBodyAndHeaders_createsWithEmptyProperties() {
        Exchange<String> ex = Exchange.of("hello", Map.of("k", "v"));
        assertThat(ex.body()).isEqualTo("hello");
        assertThat(ex.headers()).containsEntry("k", "v");
        assertThat(ex.properties()).isEmpty();
    }

    @Test
    void nullHeaders_defaultsToEmptyMap() {
        Exchange<String> ex = new Exchange<>("body", null, Map.of());
        assertThat(ex.headers()).isEmpty();
    }

    @Test
    void nullProperties_defaultsToEmptyMap() {
        Exchange<String> ex = new Exchange<>("body", Map.of(), null);
        assertThat(ex.properties()).isEmpty();
    }

    @Test
    void headers_areUnmodifiable() {
        Exchange<String> ex = Exchange.of("body", Map.of("k", "v"));
        assertThatThrownBy(() -> ex.headers().put("new", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void properties_areUnmodifiable() {
        Exchange<String> ex = new Exchange<>("body", Map.of(), Map.of("p", "val"));
        assertThatThrownBy(() -> ex.properties().put("new", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void withBody_preservesHeadersAndProperties() {
        Exchange<String> original = new Exchange<>("orig", Map.of("h", "1"), Map.of("p", "2"));
        Exchange<String> updated = original.withBody("new");
        assertThat(updated.body()).isEqualTo("new");
        assertThat(updated.headers()).isEqualTo(original.headers());
        assertThat(updated.properties()).isEqualTo(original.properties());
    }

    @Test
    void withBody_changesType() {
        Exchange<String> stringEx = Exchange.of("hello");
        Exchange<Integer> intEx = stringEx.withBody(42);
        assertThat(intEx.body()).isEqualTo(42);
    }

    @Test
    void withHeader_addsToExisting() {
        Exchange<String> ex = Exchange.of("body", Map.of("a", "1"));
        Exchange<String> updated = ex.withHeader("b", "2");
        assertThat(updated.headers()).containsEntry("a", "1").containsEntry("b", "2");
        assertThat(ex.headers()).doesNotContainKey("b");
    }

    @Test
    void withHeaders_replacesAll() {
        Exchange<String> ex = Exchange.of("body", Map.of("a", "1"));
        Exchange<String> updated = ex.withHeaders(Map.of("x", "9"));
        assertThat(updated.headers()).containsOnlyKeys("x");
        assertThat(ex.headers()).containsOnlyKeys("a");
    }

    @Test
    void withProperty_addsToExisting() {
        Exchange<String> ex = new Exchange<>("body", Map.of(), Map.of("p1", "v1"));
        Exchange<String> updated = ex.withProperty("p2", "v2");
        assertThat(updated.properties()).containsEntry("p1", "v1").containsEntry("p2", "v2");
        assertThat(ex.properties()).doesNotContainKey("p2");
    }

    @Test
    void withoutHeader_removesKey() {
        Exchange<String> ex = Exchange.of("body", Map.of("a", "1", "b", "2"));
        Exchange<String> updated = ex.withoutHeader("a");
        assertThat(updated.headers()).containsOnlyKeys("b");
        assertThat(ex.headers()).containsKeys("a", "b");
    }

    @Test
    void withoutHeader_missingKey_noOp() {
        Exchange<String> ex = Exchange.of("body", Map.of("a", "1"));
        Exchange<String> updated = ex.withoutHeader("missing");
        assertThat(updated.headers()).isEqualTo(ex.headers());
    }

    @Test
    void nullBody_isValid() {
        Exchange<String> ex = Exchange.of(null);
        assertThat(ex.body()).isNull();
    }

    @Test
    void withBody_null_isValid() {
        Exchange<String> ex = Exchange.of("hello");
        Exchange<String> updated = ex.withBody(null);
        assertThat(updated.body()).isNull();
        assertThat(updated.headers()).isEqualTo(ex.headers());
    }

    @Test
    void typedHeaderAccess() {
        Exchange<String> ex = Exchange.of("body", Map.of("count", 42));
        Integer count = ex.header("count");
        assertThat(count).isEqualTo(42);
    }

    @Test
    void typedHeaderAccess_missing_returnsNull() {
        Exchange<String> ex = Exchange.of("body");
        Integer count = ex.header("missing");
        assertThat(count).isNull();
    }

    @Test
    void typedHeaderAccess_withDefault() {
        Exchange<String> ex = Exchange.of("body");
        Integer count = ex.header("missing", 99);
        assertThat(count).isEqualTo(99);
    }

    @Test
    void typedHeaderAccess_withDefault_present() {
        Exchange<String> ex = Exchange.of("body", Map.of("count", 42));
        Integer count = ex.header("count", 99);
        assertThat(count).isEqualTo(42);
    }

    @Test
    void typedPropertyAccess() {
        Exchange<String> ex = new Exchange<>("body", Map.of(), Map.of("idx", 7));
        Integer idx = ex.property("idx");
        assertThat(idx).isEqualTo(7);
    }

    @Test
    void equality_sameFields() {
        Exchange<String> a = Exchange.of("body", Map.of("h", "v"));
        Exchange<String> b = Exchange.of("body", Map.of("h", "v"));
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equality_differentHeaders() {
        Exchange<String> a = Exchange.of("body", Map.of("h", "1"));
        Exchange<String> b = Exchange.of("body", Map.of("h", "2"));
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equality_differentBody() {
        Exchange<String> a = Exchange.of("a");
        Exchange<String> b = Exchange.of("b");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void headerOrderPreserved() {
        var headers = new LinkedHashMap<String, Object>();
        headers.put("z", "last");
        headers.put("a", "first");
        Exchange<String> ex = Exchange.of("body", headers);
        var keys = ex.headers().keySet().stream().toList();
        assertThat(keys).containsExactly("z", "a");
    }
}
