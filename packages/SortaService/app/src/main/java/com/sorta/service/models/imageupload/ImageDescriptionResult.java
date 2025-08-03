package com.sorta.service.models.imageupload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDescriptionResult {
    private String imageUrl;
    private String imageDescription;
}