package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.sorta.service.converters.APIGatewayResponseConverter;
import com.sorta.service.models.UserProfileResponse;
import com.sorta.service.processors.UserProfileProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class GetUserProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String USER_ID = "userId";

    private final UserProfileProcessor userProfileProcessor;
    private final APIGatewayResponseConverter responseConverter;

    @Inject
    public GetUserProfileHandler(UserProfileProcessor userProfileProcessor,
                                 APIGatewayResponseConverter responseConverter) {
        this.userProfileProcessor = userProfileProcessor;
        this.responseConverter = responseConverter;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        final String userId = input.getQueryStringParameters().get(USER_ID);
        final UserProfileResponse response = userProfileProcessor.getUserProfile(userId);
        return responseConverter.createSuccessResponse(response);
    }
}
