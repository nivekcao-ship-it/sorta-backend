package com.sorta.service.exceptions;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.sorta.service.converters.APIGatewayConverter;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

@Log4j2
@Singleton
public class APIGatewayExceptionTranslator {
    private final APIGatewayConverter responseConverter;

    @Inject
    public APIGatewayExceptionTranslator(final APIGatewayConverter responseConverter) {
        this.responseConverter = responseConverter;
    }
    
    public APIGatewayProxyResponseEvent translateException(final Exception exception) {
        if (exception instanceof ServiceException serviceException) {
            log.error("Service error: {}", serviceException.getMessage(), serviceException);
            return responseConverter.createErrorResponse(
                serviceException.getStatusCode(),
                serviceException.getClass().getSimpleName(),
                serviceException.getMessage()
            );
        }
        
        log.error("Unexpected error: {}", exception.getMessage(), exception);
        return responseConverter.createErrorResponse(500, 
            "Internal Server Error", "An unexpected error occurred");
    }
}