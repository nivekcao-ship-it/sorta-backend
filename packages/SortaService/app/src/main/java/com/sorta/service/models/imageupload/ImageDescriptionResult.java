package com.sorta.service.models.imageupload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDescriptionResult {
    private String imageUrl;
    private String imageDescription;
}