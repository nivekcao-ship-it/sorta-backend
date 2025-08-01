package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.converters.APIGatewayResponseConverter;
import com.sorta.service.models.SortaAgentRequest;
import com.sorta.service.models.SortaAgentResponse;
import com.sorta.service.converters.SortaAgentConverter;
import com.sorta.service.processors.SortaAgentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Named;

@Log4j2
public class SortaAgentHandler {

    private final ObjectMapper objectMapper;
    private final SortaAgentConverter sortaAgentConverter;
    private final SortaAgentProcessor sortaAgentProcessor;
    private final APIGatewayResponseConverter apiGatewayResponseConverter;

    @Inject
    public SortaAgentHandler(ObjectMapper objectMapper,
                             SortaAgentConverter sortaAgentConverter,
                             SortaAgentProcessor sortaAgentProcessor,
                             APIGatewayResponseConverter apiGatewayResponseConverter) {
        this.objectMapper = objectMapper;
        this.sortaAgentConverter = sortaAgentConverter;
        this.sortaAgentProcessor = sortaAgentProcessor;
        this.apiGatewayResponseConverter = apiGatewayResponseConverter;
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request) {
        final SortaAgentRequest sortaAgentRequest = sortaAgentConverter.toSortaAgentRequest(request);
        final SortaAgentResponse sortaAgentResponse = sortaAgentProcessor.process(sortaAgentRequest);
        return apiGatewayResponseConverter.createSuccessResponse(sortaAgentResponse);
    }
}
