package com.sorta.service.converters;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.exceptions.BadRequestException;
import com.sorta.service.models.SortaAgentMessage;
import com.sorta.service.models.SortaAgentRequest;
import com.sorta.service.models.SortaAgentResponse;
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
        final SortaAgentMessage agentResponseMsg = objectMapper.readValue(message, SortaAgentMessage.class);
        return SortaAgentResponse.builder()
                .message(agentResponseMsg)
                .sessionId(sessionId)
                .userId(userId)
                .timestamp(Instant.now().toString())
                .success(true)
                .build();
    }
}