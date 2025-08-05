package com.sorta.service.converters;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.exceptions.BadRequestException;
import com.sorta.service.models.userprofile.CreateUserProfileRequest;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class CreateUserProfileConverter {
    private final ObjectMapper objectMapper;

    @Inject
    public CreateUserProfileConverter(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CreateUserProfileRequest toCreateUserProfileRequest(final APIGatewayProxyRequestEvent request) {
        try {
            return objectMapper.readValue(request.getBody(), CreateUserProfileRequest.class);
        } catch (final JsonProcessingException e) {
            log.error("Request is not correctly formatted: {}", request.getBody());
            throw new BadRequestException("Invalid request body", e);
        }
    }
}