package com.sorta.service.exceptions;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.sorta.service.converters.APIGatewayResponseConverter;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Singleton
public class ExceptionTranslator {
    private final APIGatewayResponseConverter responseConverter;

    @Inject
    public ExceptionTranslator(APIGatewayResponseConverter responseConverter) {
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
    
    public Map<String, Object> translateExceptionToMap(final Exception exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        
        if (exception instanceof ServiceException serviceException) {
            log.error("Service error: {}", serviceException.getMessage(), serviceException);
            errorResponse.put("statusCode", serviceException.getStatusCode());
            errorResponse.put("error", serviceException.getClass().getSimpleName());
            errorResponse.put("message", serviceException.getMessage());
        } else {
            log.error("Unexpected error: {}", exception.getMessage(), exception);
            errorResponse.put("statusCode", 500);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "An unexpected error occurred");
        }
        
        return errorResponse;
    }
}