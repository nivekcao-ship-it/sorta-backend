package com.sorta.service.converters;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Singleton
public class APIGatewayConverter {
    private final ObjectMapper objectMapper;

    @Inject
    public APIGatewayConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public APIGatewayProxyResponseEvent createSuccessResponse(final Object body) {
        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(body))
                    .withHeaders(createCorsHeaders());
        } catch (Exception e) {
            log.error("Error creating success response: {}", e.getMessage(), e);
            return createErrorResponse(500, "Response Error", "Error creating response");
        }
    }

    public APIGatewayProxyResponseEvent createErrorResponse(final int statusCode, final String errorType, final String message) {
        try {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", errorType);
            errorBody.put("message", message);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withBody(objectMapper.writeValueAsString(errorBody))
                    .withHeaders(createCorsHeaders());
        } catch (Exception e) {
            log.error("Error creating error response: {}", e.getMessage(), e);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("{\"error\":\"Internal Server Error\",\"message\":\"Error creating error response\"}");
        }
    }

    private Map<String, String> createCorsHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Api-Key");
        return headers;
    }
}