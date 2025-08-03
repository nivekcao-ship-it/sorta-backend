package com.sorta.service.handlers.internal;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.sorta.service.converters.APIGatewayConverter;
import com.sorta.service.converters.ImageUploadConverter;
import com.sorta.service.models.imageupload.ImageUploadRequest;
import com.sorta.service.models.imageupload.ImageUploadResponse;
import com.sorta.service.processors.UploadImageProcessor;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

@Log4j2
public class UploadImageHandler {

    private final UploadImageProcessor uploadImageProcessor;
    private final ImageUploadConverter ImageUploadConverter;
    private final APIGatewayConverter responseConverter;

    @Inject
    public UploadImageHandler(final UploadImageProcessor uploadImageProcessor,
                              final ImageUploadConverter imageUploadConverter,
                              final APIGatewayConverter responseConverter) {
        this.uploadImageProcessor = uploadImageProcessor;
        this.ImageUploadConverter = imageUploadConverter;
        this.responseConverter = responseConverter;
    }
    
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request) {
        final ImageUploadRequest uploadRequest = ImageUploadConverter.toImageUploadRequest(request);
        final ImageUploadResponse response = uploadImageProcessor.process(uploadRequest);
        return responseConverter.createSuccessResponse(response);
    }
}
