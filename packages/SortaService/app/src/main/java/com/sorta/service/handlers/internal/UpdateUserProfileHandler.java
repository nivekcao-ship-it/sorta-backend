package com.sorta.service.handlers.internal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.sorta.service.converters.APIGatewayConverter;
import com.sorta.service.converters.UpdateUserProfileConverter;
import com.sorta.service.models.userprofile.UpdateUserProfileRequest;
import com.sorta.service.models.userprofile.UpdateUserProfileResponse;
import com.sorta.service.processors.UpdateUserProfileProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class UpdateUserProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final UpdateUserProfileProcessor processor;
    private final UpdateUserProfileConverter converter;
    private final APIGatewayConverter responseConverter;

    @Inject
    public UpdateUserProfileHandler(final UpdateUserProfileProcessor processor,
                                    final UpdateUserProfileConverter converter,
                                    final APIGatewayConverter responseConverter) {
        this.processor = processor;
        this.converter = converter;
        this.responseConverter = responseConverter;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        final UpdateUserProfileRequest request = converter.toUpdateUserProfileRequest(input);
        final UpdateUserProfileResponse response = processor.updateUserProfile(request);
        return responseConverter.createSuccessResponse(response);
    }
}