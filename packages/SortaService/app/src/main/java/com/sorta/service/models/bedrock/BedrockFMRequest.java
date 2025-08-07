package com.sorta.service.models.bedrock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BedrockFMRequest {
    @JsonProperty("anthropic_version")
    private String anthropicVersion;
    
    @JsonProperty("max_tokens")
    private int maxTokens;
    
    private List<Message> messages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private List<Content> content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Content {
        private String type;
        private String text;
        private ImageSource source;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageSource {
        private String type;
        @JsonProperty("media_type")
        private String mediaType;
        private String data;
    }
}