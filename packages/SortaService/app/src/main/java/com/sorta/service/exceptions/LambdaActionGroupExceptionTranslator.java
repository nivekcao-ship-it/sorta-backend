package com.sorta.service.exceptions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.models.LambdaActionGroupRequest;
import com.sorta.service.models.LambdaActionGroupResponse;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Log4j2
@Singleton
public class LambdaActionGroupExceptionTranslator {

    private final ObjectMapper objectMapper;

    @Inject
    public LambdaActionGroupExceptionTranslator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> translateExceptionToMap(final Exception exception,
                                                       final LambdaActionGroupRequest context) {
        String errorMessage;

        if (exception instanceof ServiceException serviceException) {
            log.error("Service error: {}", serviceException.getMessage(), serviceException);
            errorMessage = serviceException.getMessage();
        } else {
            log.error("Unexpected error: {}", exception.getMessage(), exception);
            errorMessage = "An unexpected error occurred";
        }

        final LambdaActionGroupResponse response = LambdaActionGroupResponse.builder()
            .messageVersion("1.0")
            .response(LambdaActionGroupResponse.Response.builder()
                .actionGroup(context.getActionGroup())
                .apiPath(context.getApiPath())
                .httpMethod(context.getHttpMethod())
                .httpStatusCode(500)
                .responseBody(Map.of("application/json", LambdaActionGroupResponse.ResponseContent.builder()
                    .body(errorMessage)
                    .build()))
                .build())
            .build();

        return objectMapper.convertValue(response, new TypeReference<Map<String, Object>>() {});
    }
}
