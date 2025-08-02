package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.sorta.service.converters.LambdaActionGroupConverter;
import com.sorta.service.models.LambdaActionGroupRequest;
import com.sorta.service.models.LambdaActionGroupResponse;
import com.sorta.service.models.ImageDescriptionResult;
import com.sorta.service.processors.DescribeImageProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Log4j2
public class ImageProcessingHandler {

    private final DescribeImageProcessor describeImageProcessor;
    private final LambdaActionGroupConverter lambdaActionGroupConverter;

    @Inject
    public ImageProcessingHandler(final DescribeImageProcessor describeImageProcessor, 
                                  final LambdaActionGroupConverter lambdaActionGroupConverter) {
        this.describeImageProcessor = describeImageProcessor;
        this.lambdaActionGroupConverter = lambdaActionGroupConverter;
    }

    public LambdaActionGroupResponse handleRequest(final LambdaActionGroupRequest request, final Context context) {
        log.info("Processing describe-image request");

        try {
            final List<String> imageUrls = lambdaActionGroupConverter.extractImageUrls(request);
            
            final List<ImageDescriptionResult> results = describeImageProcessor.process(imageUrls);

            return lambdaActionGroupConverter.buildSuccessResponse(request, results);
        } catch (final Exception e) {
            log.error("Error processing request: {}", e.getMessage(), e);
            return lambdaActionGroupConverter.buildErrorResponse(request, "Error processing image descriptions");
        }
    }


}