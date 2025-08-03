package com.sorta.service.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.models.bedrock.BedrockFMRequest;
import com.sorta.service.models.bedrock.BedrockFMResponse;
import com.sorta.service.models.imageupload.ImageDescriptionResult;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Singleton
@Log4j2
public class DescribeImageProcessor {

    private final ObjectMapper objectMapper;
    private final BedrockRuntimeClient bedrockClient;
    private final S3Client s3Client;

    @Inject
    public DescribeImageProcessor(final ObjectMapper objectMapper, 
                                  final BedrockRuntimeClient bedrockClient,
                                  final S3Client s3Client) {
        this.objectMapper = objectMapper;
        this.bedrockClient = bedrockClient;
        this.s3Client = s3Client;
    }

    public List<ImageDescriptionResult> process(final List<String> imageUrls) {
        log.info("Processing {} image URLs for description", imageUrls.size());
        final List<ImageDescriptionResult> results = new ArrayList<>();
        
        for (final String imageUrl : imageUrls) {
            log.debug("Processing image: {}", imageUrl);
            try {
                final String description = describeImage(imageUrl);
                log.debug("Successfully described image: {}", imageUrl);
                results.add(ImageDescriptionResult.builder()
                    .imageUrl(imageUrl)
                    .imageDescription(description)
                    .build());
            } catch (Exception e) {
                log.error("Error processing image {}: {}", imageUrl, e.getMessage(), e);
                results.add(ImageDescriptionResult.builder()
                    .imageUrl(imageUrl)
                    .imageDescription("Error processing image")
                    .build());
            }
        }
        
        log.info("Completed processing {} images", results.size());
        return results;
    }

    private String describeImage(final String imageUrl) throws Exception {
        log.debug("Starting image description for: {}", imageUrl);
        
        // Download and compress image from S3
        log.debug("Downloading and compressing image from S3: {}", imageUrl);
        final byte[] imageBytes = downloadAndCompressImage(imageUrl);
        log.debug("Downloaded and compressed {} bytes from S3", imageBytes.length);
        
        final String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        log.debug("Converted image to base64, length: {}", base64Image.length());
        
        // Prepare Claude request
        log.debug("Preparing Claude request for image analysis");
        final BedrockFMRequest requestBody = BedrockFMRequest.builder()
            .anthropicVersion("bedrock-2023-05-31")
            .maxTokens(1000)
            .messages(List.of(
                BedrockFMRequest.Message.builder()
                    .role("user")
                    .content(List.of(
                        BedrockFMRequest.Content.builder()
                            .type("image")
                            .source(BedrockFMRequest.ImageSource.builder()
                                .type("base64")
                                .mediaType("image/jpeg")
                                .data(base64Image)
                                .build())
                            .build(),
                        BedrockFMRequest.Content.builder()
                            .type("text")
                            .text("Describe this image in detail, focusing on items, room type, and storage areas.")
                            .build()
                    ))
                    .build()
            ))
            .build();

        final InvokeModelRequest request = InvokeModelRequest.builder()
            .modelId("anthropic.claude-3-haiku-20240307-v1:0")
            .body(SdkBytes.fromUtf8String(objectMapper.writeValueAsString(requestBody)))
            .build();

        // Invoke Claude
        log.debug("Invoking Claude model for image description");
            
        final InvokeModelResponse response = bedrockClient.invokeModel(request);
        log.debug("Received response from Claude model");
        
        // Parse response
        final String responseString = response.body().asUtf8String();
        log.debug("Claude response length: {} characters", responseString.length());
        
        final BedrockFMResponse responseBody = objectMapper.readValue(responseString, BedrockFMResponse.class);
        final String description = responseBody.getContent().get(0).getText();
        log.debug("Extracted description length: {} characters", description.length());
        
        return description;
    }

    private byte[] downloadAndCompressImage(String imageUrl) throws Exception {
        byte[] originalBytes = downloadImageFromS3(imageUrl);
        
        // Compress to max 512KB
        if (originalBytes.length > 512_000) {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalBytes));
            BufferedImage resized = resizeImage(img, 800, 600); // Max dimensions
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resized, "jpg", baos);
            return baos.toByteArray();
        }
        return originalBytes;
    }
    
    private BufferedImage resizeImage(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        // Calculate new dimensions maintaining aspect ratio
        double widthRatio = (double) maxWidth / width;
        double heightRatio = (double) maxHeight / height;
        double ratio = Math.min(widthRatio, heightRatio);
        
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);
        
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return resized;
    }

    private byte[] downloadImageFromS3(final String imageUrl) throws Exception {
        String cleanUrl = imageUrl.trim();
        if (cleanUrl.startsWith("[") && cleanUrl.endsWith("]")) {
            cleanUrl = cleanUrl.substring(1, cleanUrl.length() - 1);
        }
        final URI uri = URI.create(cleanUrl);
        final String bucket = uri.getHost().split("\\.")[0];
        final String key = uri.getPath().substring(1);
        
        log.debug("Downloading from S3 - bucket: {}, key: {}", bucket, key);
        
        final GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
            
        final byte[] imageBytes = s3Client.getObject(request).readAllBytes();
        log.debug("Successfully downloaded {} bytes from S3", imageBytes.length);
        
        return imageBytes;
    }
}
