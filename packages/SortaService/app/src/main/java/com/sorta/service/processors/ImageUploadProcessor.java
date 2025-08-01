package com.sorta.service.processors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.exceptions.BadRequestException;
import com.sorta.service.exceptions.InternalServerException;
import com.sorta.service.models.ImageUploadRequest;
import com.sorta.service.models.ImageUploadResponse;
import com.sorta.service.utils.S3KeyGenerator;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Singleton
@Log4j2
public class ImageUploadProcessor {
    private final ObjectMapper objectMapper;
    private final S3KeyGenerator s3KeyGenerator;
    private final S3Presigner s3Presigner;
    private final String declutterImagesBucket;

    @Inject
    public ImageUploadProcessor(final ObjectMapper objectMapper,
                                final S3KeyGenerator s3KeyGenerator,
                                final S3Presigner s3Presigner,
                                @Named("declutterImagesBucketName") final String declutterImagesBucket) {
        this.objectMapper = objectMapper;
        this.s3KeyGenerator = s3KeyGenerator;
        this.s3Presigner = s3Presigner;
        this.declutterImagesBucket = declutterImagesBucket;
    }

    public ImageUploadResponse process(final ImageUploadRequest request) {
        try {
            if (request.getFileType() == null || request.getPurpose() == null) {
                throw new BadRequestException("fileType and purpose are required");
            }

            final String key = generateS3Key(request);

            final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(declutterImagesBucket)
                    .key(key)
                    .contentType(request.getFileType())
                    .metadata(buildImageMetadata(request))
                    .build();

            final PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .putObjectRequest(putObjectRequest)
                    .build();

            final PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            final String uploadUrl = presignedRequest.url().toString();
            final String imageUrl = String.format("https://%s.s3.amazonaws.com/%s", declutterImagesBucket, key);

            return ImageUploadResponse.builder()
                    .uploadUrl(uploadUrl)
                    .imageUrl(imageUrl)
                    .expiresIn(300)
                    .build();
        } catch (Exception e) {
            throw new InternalServerException("Failed to create presigned URL: " + e.getMessage());
        }
    }

    private String generateS3Key(final ImageUploadRequest request) {
        String userId = request.getUserId();
        String sessionId = request.getSessionId();
        String fileExtension = s3KeyGenerator.getFileExtension(request.getFileType());
        
        return switch (request.getPurpose()) {
            case "SPACE_PHOTO" -> s3KeyGenerator.spacePhoto(userId, sessionId, UUID.randomUUID().toString(), fileExtension);
            case "BEFORE_PHOTO" -> s3KeyGenerator.beforePhoto(userId, sessionId, fileExtension);
            case "AFTER_PHOTO" -> s3KeyGenerator.afterPhoto(userId, sessionId, fileExtension);
            default -> throw new BadRequestException("Unsupported image upload purpose.");
        };
    }

    private Map<String, String> buildImageMetadata(final ImageUploadRequest request) {
        return objectMapper.convertValue(request, new TypeReference<>() {});
    }
}
