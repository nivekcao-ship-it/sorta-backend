package com.sorta.service.handlers.internal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.sorta.service.converters.APIGatewayConverter;
import com.sorta.service.converters.PatchUserProfileConverter;
import com.sorta.service.models.userprofile.PatchUserProfileRequest;
import com.sorta.service.models.userprofile.PatchUserProfileResponse;
import com.sorta.service.processors.PatchUserProfileProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class PatchUserProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final PatchUserProfileProcessor processor;
    private final PatchUserProfileConverter converter;
    private final APIGatewayConverter responseConverter;

    @Inject
    public PatchUserProfileHandler(final PatchUserProfileProcessor processor,
                                   final PatchUserProfileConverter converter,
                                   final APIGatewayConverter responseConverter) {
        this.processor = processor;
        this.converter = converter;
        this.responseConverter = responseConverter;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        final PatchUserProfileRequest request = converter.toUpdateUserProfileRequest(input);
        final PatchUserProfileResponse response = processor.updateUserProfile(request);
        return responseConverter.createSuccessResponse(response);
    }
}