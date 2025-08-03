package com.sorta.service.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageUploadResponse {
    String uploadUrl;
    String imageUrl;
    int expiresIn;
}