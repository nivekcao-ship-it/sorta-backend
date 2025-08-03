package com.sorta.service.handlers.internal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.converters.APIGatewayConverter;
import com.sorta.service.models.imageupload.ImageDescriptionResult;
import com.sorta.service.processors.DescribeImageProcessor;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class SandboxHandler {
    private final ObjectMapper objectMapper;
    private final DescribeImageProcessor describeImageProcessor;
    private final APIGatewayConverter apiGatewayConverter;

    @Inject
    public SandboxHandler(final ObjectMapper objectMapper,
                          final DescribeImageProcessor describeImageProcessor,
                          final APIGatewayConverter apiGatewayConverter) {
        this.objectMapper = objectMapper;
        this.describeImageProcessor = describeImageProcessor;
        this.apiGatewayConverter = apiGatewayConverter;
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request, final Context context) throws JsonProcessingException {
        Map<String, List<String>> input = objectMapper.readValue(request.getBody(), new TypeReference<>() {
        });
        List<ImageDescriptionResult> results = this.describeImageProcessor.process(input.get("image"));
        return apiGatewayConverter.createSuccessResponse(results);
    }
}
