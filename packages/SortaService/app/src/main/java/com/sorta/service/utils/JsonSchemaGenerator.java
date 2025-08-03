package com.sorta.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JsonSchemaGenerator {
    
    private final ObjectMapper objectMapper;
    private final com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator schemaGenerator;
    
    @Inject
    public JsonSchemaGenerator(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.schemaGenerator = new com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator(objectMapper);
    }
    
    /**
     * Convert POJO to JSON Schema using Jackson's schema generation
     */
    public JsonNode convertToSchema(Class<?> pojoClass) {
        try {
            JsonSchema schema = schemaGenerator.generateSchema(pojoClass);
            return objectMapper.valueToTree(schema);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate schema for class: " + pojoClass.getName(), e);
        }
    }
}