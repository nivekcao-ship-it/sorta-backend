package com.sorta.service.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageUploadRequest {
    String userId;
    String sessionId;
    String fileType;
    String purpose;
}