package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.converters.APIGatewayResponseConverter;
import com.sorta.service.models.ImageDescriptionResult;
import com.sorta.service.processors.DescribeImageProcessor;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class SandboxHandler {
    private final ObjectMapper objectMapper;
    private final DescribeImageProcessor describeImageProcessor;
    private final APIGatewayResponseConverter apiGatewayResponseConverter;

    @Inject
    public SandboxHandler(final ObjectMapper objectMapper,
                          final DescribeImageProcessor describeImageProcessor,
                          final APIGatewayResponseConverter apiGatewayResponseConverter) {
        this.objectMapper = objectMapper;
        this.describeImageProcessor = describeImageProcessor;
        this.apiGatewayResponseConverter = apiGatewayResponseConverter;
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request, final Context context) throws JsonProcessingException {
        Map<String, List<String>> input = objectMapper.readValue(request.getBody(), new TypeReference<>() {
        });
        List<ImageDescriptionResult> results = this.describeImageProcessor.process(input.get("image"));
        return apiGatewayResponseConverter.createSuccessResponse(results);
    }
}
