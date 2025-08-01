package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.sorta.service.converters.APIGatewayResponseConverter;
import com.sorta.service.converters.ImageUploadConverter;
import com.sorta.service.models.ImageUploadRequest;
import com.sorta.service.models.ImageUploadResponse;
import com.sorta.service.processors.ImageUploadProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class ImageUploadHandler {

    private final ImageUploadProcessor imageUploadProcessor;
    private final ImageUploadConverter ImageUploadConverter;
    private final APIGatewayResponseConverter responseConverter;

    @Inject
    public ImageUploadHandler(final ImageUploadProcessor imageUploadProcessor,
                              final ImageUploadConverter imageUploadConverter,
                              final APIGatewayResponseConverter responseConverter) {
        this.imageUploadProcessor = imageUploadProcessor;
        this.ImageUploadConverter = imageUploadConverter;
        this.responseConverter = responseConverter;
    }
    
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request) {
        final ImageUploadRequest uploadRequest = ImageUploadConverter.toImageUploadRequest(request);
        final ImageUploadResponse response = imageUploadProcessor.process(uploadRequest);
        return responseConverter.createSuccessResponse(response);
    }
}
