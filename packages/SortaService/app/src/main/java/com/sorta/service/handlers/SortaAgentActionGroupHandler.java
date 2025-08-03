package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.sorta.service.converters.ActionGroupConverter;
import com.sorta.service.dagger.AppComponent;
import com.sorta.service.exceptions.LambdaActionGroupExceptionTranslator;
import com.sorta.service.exceptions.NotFoundException;
import com.sorta.service.handlers.internal.DescribeImageHandler;
import com.sorta.service.models.agentactiongroup.LambdaActionGroupRequest;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class SortaAgentActionGroupHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private static final AppComponent APP_COMPONENT = AppComponent.getInstance();
    private static final String HTTP_POST = "POST";

    @Inject
    DescribeImageHandler describeImageHandler;
    @Inject
    ActionGroupConverter actionGroupConverter;
    @Inject LambdaActionGroupExceptionTranslator APIGatewayExceptionTranslator;

    public SortaAgentActionGroupHandler() {
        APP_COMPONENT.inject(this);
    }

    @Override
    public Map<String, Object> handleRequest(final Map<String, Object> input, final Context context) {
        log.info("Processing Bedrock agent action group request: {}", input);
        final LambdaActionGroupRequest request = actionGroupConverter.fromMap(input);

        try {
            final String apiPath = Optional.ofNullable(request.getApiPath()).orElse("");
            final String httpMethod = Optional.ofNullable(request.getHttpMethod()).orElse("");

            if ("/describe-image".equals(apiPath) && HTTP_POST.equals(httpMethod)) {
                return actionGroupConverter.toMap(describeImageHandler.handleRequest(request, context));
            } else {
                throw new NotFoundException("Action group endpoint not found: " + apiPath);
            }
        } catch (final Exception e) {
            log.error("Error processing action group request: {}", e.getMessage(), e);
            return APIGatewayExceptionTranslator.translateExceptionToMap(e, request);
        }
    }
}
