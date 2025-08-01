package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.sorta.service.dagger.AppComponent;
import com.sorta.service.exceptions.ExceptionTranslator;
import com.sorta.service.exceptions.NotFoundException;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.util.Map;

@Log4j2
public class SortaAgentActionGroupHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private static final AppComponent APP_COMPONENT = AppComponent.getInstance();
    private static final String HTTP_POST = "POST";

    @Inject ImageProcessingHandler imageProcessingHandler;
    @Inject ExceptionTranslator exceptionTranslator;

    public SortaAgentActionGroupHandler() {
        APP_COMPONENT.inject(this);
    }

    @Override
    public Map<String, Object> handleRequest(final Map<String, Object> input, final Context context) {
        log.info("Processing Bedrock agent action group request: {}", input);
        
        try {
            final String apiPath = getStringValue(input, "apiPath", "");
            final String httpMethod = getStringValue(input, "httpMethod", "");

            if ("/describe-image".equals(apiPath) && HTTP_POST.equals(httpMethod)) {
                return imageProcessingHandler.handleRequest(input, context);
            } else {
                throw new NotFoundException("Action group endpoint not found: " + apiPath);
            }
        } catch (Exception e) {
            log.error("Error processing action group request: {}", e.getMessage(), e);
            return exceptionTranslator.translateExceptionToMap(e);
        }
    }

    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }
}