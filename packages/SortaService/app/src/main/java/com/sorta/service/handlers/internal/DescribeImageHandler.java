package com.sorta.service.handlers.internal;

import com.amazonaws.services.lambda.runtime.Context;
import com.sorta.service.converters.ActionGroupConverter;
import com.sorta.service.models.agentactiongroup.LambdaActionGroupRequest;
import com.sorta.service.models.agentactiongroup.LambdaActionGroupResponse;
import com.sorta.service.models.imageupload.ImageDescriptionResult;
import com.sorta.service.processors.DescribeImageProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Log4j2
public class DescribeImageHandler {

    private final DescribeImageProcessor describeImageProcessor;
    private final ActionGroupConverter actionGroupConverter;

    @Inject
    public DescribeImageHandler(final DescribeImageProcessor describeImageProcessor,
                                final ActionGroupConverter actionGroupConverter) {
        this.describeImageProcessor = describeImageProcessor;
        this.actionGroupConverter = actionGroupConverter;
    }

    public LambdaActionGroupResponse handleRequest(final LambdaActionGroupRequest request, final Context context) {
        log.info("Processing describe-image request");

        try {
            final List<String> imageUrls = actionGroupConverter.extractImageUrls(request);
            
            final List<ImageDescriptionResult> results = describeImageProcessor.process(imageUrls);

            return actionGroupConverter.buildSuccessResponse(request, results);
        } catch (final Exception e) {
            log.error("Error processing request: {}", e.getMessage(), e);
            return actionGroupConverter.buildErrorResponse(request, "Error processing image descriptions");
        }
    }


}