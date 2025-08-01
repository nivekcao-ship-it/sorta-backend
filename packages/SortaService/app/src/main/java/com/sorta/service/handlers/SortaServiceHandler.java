package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.dagger.AppComponent;
import com.sorta.service.exceptions.ExceptionTranslator;
import com.sorta.service.exceptions.NotFoundException;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class SortaServiceHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final AppComponent APP_COMPONENT = AppComponent.getInstance();
    private static final String HTTP_POST = "POST";
    private static final String HTTP_GET = "GET";

    @Inject SortaAgentHandler sortaAgentHandler;
    @Inject ImageUploadHandler imageUploadHandler;
    @Inject UserProfileHandler userProfileHandler;
    @Inject ExceptionTranslator exceptionTranslator;

    public SortaServiceHandler() {
        // Initialize dependency injection in constructor
        APP_COMPONENT.inject(this);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request, final Context context) {
        log.info("Processing API Gateway request: {}", request);
        
        try {
            final String path = request.getPath();
            final String httpMethod = request.getHttpMethod();

            if (path.contains("/agent/conversation") && HTTP_POST.equals(httpMethod)) {
                return sortaAgentHandler.handleRequest(request);
            } else if (path.contains("/images/presigned-upload-url") && HTTP_POST.equals(httpMethod)) {
                return imageUploadHandler.handleRequest(request);
            } else if (path.contains("/users/profile") && HTTP_GET.equals(httpMethod)) {
                return userProfileHandler.handleRequest(request, context);
            } else {
                throw new NotFoundException("Endpoint not found: " + path);
            }
        } catch (Exception e) {
            log.error("Error processing request: {}", e.getMessage(), e);
            return exceptionTranslator.translateException(e);
        }
    }
}
