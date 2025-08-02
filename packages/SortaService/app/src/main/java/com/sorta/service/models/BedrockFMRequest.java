package com.sorta.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BedrockFMRequest {
    @JsonProperty("anthropic_version")
    private String anthropicVersion;
    
    @JsonProperty("max_tokens")
    private int maxTokens;
    
    private List<Message> messages;
    
    @Data
    @Builder
    public static class Message {
        private String role;
        private List<Content> content;
    }
    
    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Content {
        private String type;
        private String text;
        private ImageSource source;
    }
    
    @Data
    @Builder
    public static class ImageSource {
        private String type;
        @JsonProperty("media_type")
        private String mediaType;
        private String data;
    }
}