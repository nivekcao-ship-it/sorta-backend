package com.sorta.service.handlers.internal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.sorta.service.converters.APIGatewayConverter;
import com.sorta.service.converters.CreateUserProfileConverter;
import com.sorta.service.models.userprofile.CreateUserProfileRequest;
import com.sorta.service.models.userprofile.CreateUserProfileResponse;
import com.sorta.service.processors.CreateUserProfileProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class CreateUserProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CreateUserProfileProcessor processor;
    private final CreateUserProfileConverter converter;
    private final APIGatewayConverter responseConverter;

    @Inject
    public CreateUserProfileHandler(final CreateUserProfileProcessor processor,
                                   final CreateUserProfileConverter converter,
                                   final APIGatewayConverter responseConverter) {
        this.processor = processor;
        this.converter = converter;
        this.responseConverter = responseConverter;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        final CreateUserProfileRequest request = converter.toCreateUserProfileRequest(input);
        final CreateUserProfileResponse response = processor.createUserProfile(request);
        return responseConverter.createSuccessResponse(response);
    }
}