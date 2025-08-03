package com.sorta.service.handlers.internal;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.sorta.service.converters.APIGatewayConverter;
import com.sorta.service.models.userprofile.GetUserProfileResponse;
import com.sorta.service.processors.GetUserProfileProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class GetUserProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String USER_ID = "userId";

    private final GetUserProfileProcessor getUserProfileProcessor;
    private final APIGatewayConverter responseConverter;

    @Inject
    public GetUserProfileHandler(GetUserProfileProcessor getUserProfileProcessor,
                                 APIGatewayConverter responseConverter) {
        this.getUserProfileProcessor = getUserProfileProcessor;
        this.responseConverter = responseConverter;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        final String userId = input.getQueryStringParameters().get(USER_ID);
        final GetUserProfileResponse response = getUserProfileProcessor.getUserProfile(userId);
        return responseConverter.createSuccessResponse(response);
    }
}
