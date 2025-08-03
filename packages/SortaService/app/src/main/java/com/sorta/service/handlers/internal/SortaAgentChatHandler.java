package com.sorta.service.handlers.internal;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.converters.APIGatewayConverter;
import com.sorta.service.models.agent.SortaAgentRequest;
import com.sorta.service.models.agent.SortaAgentResponse;
import com.sorta.service.converters.SortaAgentConverter;
import com.sorta.service.processors.SortaAgentProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class SortaAgentChatHandler {

    private final ObjectMapper objectMapper;
    private final SortaAgentConverter sortaAgentConverter;
    private final SortaAgentProcessor sortaAgentProcessor;
    private final APIGatewayConverter apiGatewayConverter;

    @Inject
    public SortaAgentChatHandler(ObjectMapper objectMapper,
                                 SortaAgentConverter sortaAgentConverter,
                                 SortaAgentProcessor sortaAgentProcessor,
                                 APIGatewayConverter apiGatewayConverter) {
        this.objectMapper = objectMapper;
        this.sortaAgentConverter = sortaAgentConverter;
        this.sortaAgentProcessor = sortaAgentProcessor;
        this.apiGatewayConverter = apiGatewayConverter;
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request) {
        final SortaAgentRequest sortaAgentRequest = sortaAgentConverter.toSortaAgentRequest(request);
        final SortaAgentResponse sortaAgentResponse = sortaAgentProcessor.process(sortaAgentRequest);
        return apiGatewayConverter.createSuccessResponse(sortaAgentResponse);
    }
}
