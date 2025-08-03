package com.sorta.service.models.imageupload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageUploadResponse {
    String uploadUrl;
    String imageUrl;
    int expiresIn;
}