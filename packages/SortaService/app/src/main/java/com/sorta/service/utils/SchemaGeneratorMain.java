package com.sorta.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.models.agent.SortaAgentMessage;

import java.io.File;
import java.io.IOException;

public class SchemaGeneratorMain {
    public static void main(String[] args) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonSchemaGenerator generator = new JsonSchemaGenerator(mapper);
        final JsonNode schema = generator.convertToSchema(SortaAgentMessage.class);
        final JsonNode sanitizedSchema = schema.get("properties");
        
        final String outputPath = args.length > 0 ? args[0] : "schema.json";
        final File outputFile = new File(outputPath);
        
        // Create parent directories if they don't exist
        final File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        mapper.writeValue(outputFile, sanitizedSchema);
        System.out.println("Schema generated: " + outputPath);
    }
}