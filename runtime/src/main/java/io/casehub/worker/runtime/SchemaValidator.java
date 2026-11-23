package io.casehub.worker.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import io.casehub.worker.api.Capability;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class SchemaValidator {

    private static final String EMPTY_SCHEMA = "{}";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SchemaRegistry schemaRegistry =
        SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
    private final ConcurrentHashMap<String, Schema> cache = new ConcurrentHashMap<>();

    public void ensureSchemaParsed(String schema) {
        if (EMPTY_SCHEMA.equals(schema)) return;
        cache.computeIfAbsent(schema, this::parseSchema);
    }

    public Optional<String> validateInput(Capability capability, Object input) {
        return validate(capability.inputProjection(), input);
    }

    public Optional<String> validateOutput(Capability capability, Object output) {
        return validate(capability.outputProjection(), output);
    }

    private Optional<String> validate(String schemaString, Object data) {
        if (EMPTY_SCHEMA.equals(schemaString)) {return Optional.empty();}
        Schema                                           schema = cache.computeIfAbsent(schemaString, this::parseSchema);
        JsonNode                                         node   = objectMapper.valueToTree(data);
        java.util.Collection<com.networknt.schema.Error>  errors = schema.validate(node);
        if (errors.isEmpty()) {return Optional.empty();}
        String message = errors.stream()
                               .map(com.networknt.schema.Error::getMessage)
                               .collect(Collectors.joining("\n"));
        return Optional.of(message);
    }

    private Schema parseSchema(String schemaString) {
        try {
            JsonNode schemaNode = objectMapper.readTree(schemaString);
            return schemaRegistry.getSchema(schemaNode);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Malformed JSON Schema: " + e.getMessage(), e);
        }
    }
}
