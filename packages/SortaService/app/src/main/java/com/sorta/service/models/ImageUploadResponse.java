package com.sorta.service.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImageUploadResponse {
    String uploadUrl;
    String imageUrl;
    int expiresIn;
}