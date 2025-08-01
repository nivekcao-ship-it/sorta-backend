package com.sorta.service.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import javax.inject.Inject;

@Log4j2
public class ImageEventHandler implements RequestHandler<S3Event, Void> {
    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    @Inject
    public ImageEventHandler(S3Client s3Client, ObjectMapper objectMapper) {
        this.s3Client = s3Client;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Void handleRequest(final S3Event s3Event, final Context context) {
        try {
            for (S3EventNotification.S3EventNotificationRecord record : s3Event.getRecords()) {
                processS3Record(record);
            }
            return null;
        } catch (Exception e) {
            log.error("Error processing S3 event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process S3 event", e);
        }
    }
    
    private void processS3Record(S3EventNotification.S3EventNotificationRecord record) {
        final String eventName = record.getEventName();
        final String bucketName = record.getS3().getBucket().getName();
        final String objectKey = record.getS3().getObject().getKey();
        
        log.info("Processing S3 event: {} for object: s3://{}/{}", eventName, bucketName, objectKey);
        
        if (eventName.startsWith("ObjectCreated")) {
            handleImageUploadComplete(bucketName, objectKey);
        } else if (eventName.startsWith("ObjectRemoved")) {
            handleImageDeleted(bucketName, objectKey);
        }
    }
    
    private void handleImageUploadComplete(final String bucketName, final String objectKey) {
        try {
            // Get object metadata
            HeadObjectResponse headResponse = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build());
            
            log.info("Image upload completed: s3://{}/{}", bucketName, objectKey);
            log.info("Content type: {}, Size: {} bytes", headResponse.contentType(), headResponse.contentLength());
            
            // Extract metadata if available
            if (headResponse.metadata() != null) {
                String userId = headResponse.metadata().get("userId");
                String sessionId = headResponse.metadata().get("sessionId");
                String purpose = headResponse.metadata().get("purpose");
                
                log.info("Image metadata - userId: {}, sessionId: {}, purpose: {}", userId, sessionId, purpose);
                
                // Process based on image purpose
                processImageByPurpose(bucketName, objectKey, purpose, userId, sessionId);
            }
            
        } catch (final Exception e) {
            log.error("Error processing image event", e);
        }
    }
    
    private void processImageByPurpose(final String bucketName, final String objectKey, final String purpose,
                                       final String userId, final String sessionId) {
        switch (purpose) {
            case "before_photo" -> {
                log.info("Processing before photo for session: {}", sessionId);
                // TODO: Trigger update session status, etc.
            }
            case "after_photo" -> {
                log.info("Processing after photo for session: {}", sessionId);
                // TODO: Trigger update session status, etc.
            }
            case "space_photo" -> {
                log.info("Processing space photo for user: {}", userId);
                // TODO: Analyze space, generate description, etc.
            }
            default -> log.info("Unknown image purpose: {}", purpose);
        }
    }
    
    private void handleImageDeleted(final String bucketName, final String objectKey) {
        log.info("Image deleted: s3://{}/{}", bucketName, objectKey);
        // TODO: Clean up related data, update database, etc.
    }
}