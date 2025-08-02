package com.sorta.service.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.models.LambdaActionGroupRequest;
import com.sorta.service.models.LambdaActionGroupResponse;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Singleton
@Log4j2
public class LambdaActionGroupConverter {

    private final ObjectMapper objectMapper;

    @Inject
    public LambdaActionGroupConverter(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public LambdaActionGroupRequest fromMap(final Map<String, Object> input) {
        return objectMapper.convertValue(input, LambdaActionGroupRequest.class);
    }

    public Map<String, Object> toMap(final LambdaActionGroupResponse response) {
        return objectMapper.convertValue(response, new TypeReference<Map<String, Object>>() {});
    }

    public List<String> extractImageUrls(final LambdaActionGroupRequest request) {
        if (request.getRequestBody() == null || request.getRequestBody().getContent() == null) {
            return new ArrayList<>();
        }

        return request.getRequestBody().getContent().values().stream()
            .flatMap(contentType -> contentType.getProperties().stream())
            .filter(property -> "imageUrl".equals(property.getName()))
            .flatMap(property -> {
                String value = property.getValue();
                if ("array".equals(property.getType()) && value.startsWith("[") && value.endsWith("]")) {
                    String content = value.substring(1, value.length() - 1).trim();
                    if (content.isEmpty()) {
                        return Stream.<String>of();
                    }
                    return Stream.of(content.split(","))
                        .map(String::trim)
                        .filter(url -> !url.isEmpty());
                } else {
                    return Stream.of(value);
                }
            })
            .toList();
    }

    public LambdaActionGroupResponse buildSuccessResponse(final LambdaActionGroupRequest request,
                                                          final Object responseBody) throws JsonProcessingException {
        final String responseBodyString = objectMapper.writeValueAsString(responseBody);
        return LambdaActionGroupResponse.builder()
            .messageVersion("1.0")
            .response(LambdaActionGroupResponse.Response.builder()
                .actionGroup(request.getActionGroup())
                .apiPath(request.getApiPath())
                .httpMethod(request.getHttpMethod())
                .httpStatusCode(200)
                .responseBody(Map.of("application/json", LambdaActionGroupResponse.ResponseContent.builder()
                    .body(responseBodyString)
                    .build()))
                .build())
            .sessionAttributes(request.getSessionAttributes())
            .promptSessionAttributes(request.getPromptSessionAttributes())
            .build();
    }

    public LambdaActionGroupResponse buildErrorResponse(LambdaActionGroupRequest request, String errorMessage) {
        return LambdaActionGroupResponse.builder()
            .messageVersion("1.0")
            .response(LambdaActionGroupResponse.Response.builder()
                .actionGroup(request.getActionGroup())
                .apiPath(request.getApiPath())
                .httpMethod(request.getHttpMethod())
                .httpStatusCode(500)
                .responseBody(Map.of("application/json", LambdaActionGroupResponse.ResponseContent.builder()
                    .body(errorMessage)
                    .build()))
                .build())
            .sessionAttributes(request.getSessionAttributes())
            .promptSessionAttributes(request.getPromptSessionAttributes())
            .build();
    }
}