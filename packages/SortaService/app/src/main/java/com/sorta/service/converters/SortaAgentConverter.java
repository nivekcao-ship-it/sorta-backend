package com.sorta.service.converters;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.exceptions.BadRequestException;
import com.sorta.service.models.agent.SortaAgentMessage;
import com.sorta.service.models.agent.SortaAgentRequest;
import com.sorta.service.models.agent.SortaAgentResponse;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;

@Log4j2
@Singleton
public class SortaAgentConverter {
    private final ObjectMapper objectMapper;

    @Inject
    public SortaAgentConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SortaAgentRequest toSortaAgentRequest(final APIGatewayProxyRequestEvent request) {
        try {
            return objectMapper.readValue(request.getBody(), SortaAgentRequest.class);
        } catch (final JsonProcessingException e) {
            log.error("Request is not correctly formatted: {}", request.getBody());
            throw new BadRequestException("Invalid request body", e);
        }
    }
    
    public SortaAgentResponse toSortaAgentResponse(final String message,
                                                   final String sessionId,
                                                   final String userId) throws JsonProcessingException {
        String jsonPart = extractJson(message);
        final SortaAgentMessage agentResponseMsg = objectMapper.readValue(jsonPart, SortaAgentMessage.class);
        return SortaAgentResponse.builder()
                .message(agentResponseMsg)
                .sessionId(sessionId)
                .userId(userId)
                .timestamp(Instant.now().toString())
                .success(true)
                .build();
    }

    private String extractJson(String response) {
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }
}