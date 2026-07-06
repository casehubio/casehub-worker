package io.casehub.worker.runtime;

import io.casehub.worker.api.Capability;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SchemaValidatorTest {

    private final SchemaValidator validator = new SchemaValidator();

    private static final String REQUIRE_NAME_SCHEMA = """
        {
          "type": "object",
          "properties": {
            "name": { "type": "string" }
          },
          "required": ["name"]
        }""";

    private static final String REQUIRE_RESULT_SCHEMA = """
        {
          "type": "object",
          "properties": {
            "result": { "type": "number" }
          },
          "required": ["result"]
        }""";

    @Test
    void validateInput_validData_returnsEmpty() {
        Capability cap = Capability.of("c", REQUIRE_NAME_SCHEMA, "{}");
        Optional<String> result = validator.validateInput(cap, Map.of("name", "Alice"));
        assertThat(result).isEmpty();
    }

    @Test
    void validateInput_missingRequiredField_returnsError() {
        Capability cap = Capability.of("c", REQUIRE_NAME_SCHEMA, "{}");
        Optional<String> result = validator.validateInput(cap, Map.of("age", 30));
        assertThat(result).isPresent();
        assertThat(result.get()).contains("name");
    }

    @Test
    void validateInput_wrongType_returnsError() {
        Capability cap = Capability.of("c", REQUIRE_NAME_SCHEMA, "{}");
        Optional<String> result = validator.validateInput(cap, Map.of("name", 42));
        assertThat(result).isPresent();
        assertThat(result.get()).containsIgnoringCase("string");
    }

    @Test
    void validateOutput_validData_returnsEmpty() {
        Capability cap = Capability.of("c", "{}", REQUIRE_RESULT_SCHEMA);
        Optional<String> result = validator.validateOutput(cap, Map.of("result", 42));
        assertThat(result).isEmpty();
    }

    @Test
    void validateOutput_invalidData_returnsError() {
        Capability cap = Capability.of("c", "{}", REQUIRE_RESULT_SCHEMA);
        Optional<String> result = validator.validateOutput(cap, Map.of("result", "not-a-number"));
        assertThat(result).isPresent();
    }

    @Test
    void validateInput_emptySchema_skipsValidation() {
        Capability cap = Capability.of("c", "{}", "{}");
        Optional<String> result = validator.validateInput(cap, Map.of("anything", "goes"));
        assertThat(result).isEmpty();
    }

    @Test
    void validateOutput_emptySchema_skipsValidation() {
        Capability cap = Capability.of("c", "{}", "{}");
        Optional<String> result = validator.validateOutput(cap, Map.of("anything", "goes"));
        assertThat(result).isEmpty();
    }

    @Test
    void ensureSchemaParsed_validSchema_noException() {
        validator.ensureSchemaParsed(REQUIRE_NAME_SCHEMA);
    }

    @Test
    void ensureSchemaParsed_emptySchema_noException() {
        validator.ensureSchemaParsed("{}");
    }

    @Test
    void ensureSchemaParsed_malformedSchema_throwsIAE() {
        assertThatThrownBy(() -> validator.ensureSchemaParsed("not valid json"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Malformed JSON Schema");
    }

    @Test
    void ensureSchemaParsed_cachesSchema() {
        validator.ensureSchemaParsed(REQUIRE_NAME_SCHEMA);
        Capability cap = Capability.of("c", REQUIRE_NAME_SCHEMA, "{}");
        Optional<String> result = validator.validateInput(cap, Map.of("name", "Alice"));
        assertThat(result).isEmpty();
    }
}
