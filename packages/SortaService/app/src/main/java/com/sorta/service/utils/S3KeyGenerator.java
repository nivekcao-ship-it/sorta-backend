package com.sorta.service.utils;

public class S3KeyGenerator {
    
    public String spacePhoto(final String userId, final String spaceId, final String photoId, final String fileExtension) {
        return String.format("spaces/%s/%s/%s.%s", userId, spaceId, photoId, fileExtension);
    }
    
    public String beforePhoto(final String userId, final String sessionId, final String fileExtension) {
        return String.format("sessions/%s/%s/before.%s", userId, sessionId, fileExtension);
    }
    
    public String afterPhoto(final String userId, final String sessionId, final String fileExtension) {
        return String.format("sessions/%s/%s/after.%s", userId, sessionId, fileExtension);
    }
    
    public String getFileExtension(final String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            default -> "jpg";
        };
    }
}