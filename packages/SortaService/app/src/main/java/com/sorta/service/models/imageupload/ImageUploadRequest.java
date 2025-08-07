package com.sorta.service.models.imageupload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadRequest {
    @JsonProperty(required = true)
    String userId;
    @JsonProperty(required = true)
    String sessionId;
    @JsonProperty(required = true)
    String fileType;
    @JsonProperty(required = true)
    String purpose;
}