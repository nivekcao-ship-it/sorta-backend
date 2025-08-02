package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.models.ImageDescriptionResult;
import com.sorta.service.processors.DescribeImageProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
@Log4j2
public class ImageProcessingHandler {

    private final DescribeImageProcessor describeImageProcessor;
    private final ObjectMapper objectMapper;

    @Inject
    public ImageProcessingHandler(final DescribeImageProcessor describeImageProcessor, final ObjectMapper objectMapper) {
        this.describeImageProcessor = describeImageProcessor;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> handleRequest(final Map<String, Object> input, final Context context) {
        log.info("Processing describe-image request");

        // Extract imageUrl from request body
        List<String> imageUrls = extractImageUrls(input);
        
        List<ImageDescriptionResult> results = describeImageProcessor.process(imageUrls);
        
        return buildResponse(results);
    }

    private List<String> extractImageUrls(Map<String, Object> input) {
        Object requestBodyObj = input.get("requestBody");
        if (!(requestBodyObj instanceof Map)) {
            return new ArrayList<>();
        }
        
        Map<String, Object> requestBody = (Map<String, Object>) requestBodyObj;
        Object contentObj = requestBody.get("content");
        if (!(contentObj instanceof Map)) {
            return new ArrayList<>();
        }
        
        Map<String, Object> content = (Map<String, Object>) contentObj;
        Object jsonContent = content.get("application/json");
        if (!(jsonContent instanceof Map)) {
            return new ArrayList<>();
        }
        
        Map<String, Object> jsonData = (Map<String, Object>) jsonContent;
        Object imageUrlObj = jsonData.get("imageUrl");
        if (imageUrlObj instanceof List) {
            return (List<String>) imageUrlObj;
        }
        
        return new ArrayList<>();
    }

    private Map<String, Object> buildResponse(List<ImageDescriptionResult> results) {
        List<Map<String, Object>> responseArray = results.stream()
            .map(result -> objectMapper.convertValue(result, new TypeReference<Map<String, Object>>() {}))
            .toList();
        
        return Map.of("body", responseArray);
    }

}