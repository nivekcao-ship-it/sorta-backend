package com.sorta.service.models.imageupload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {
    String uploadUrl;
    String imageUrl;
    int expiresIn;
}