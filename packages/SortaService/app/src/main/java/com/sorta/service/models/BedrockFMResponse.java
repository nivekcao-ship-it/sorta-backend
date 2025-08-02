package com.sorta.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BedrockFMResponse {
    private List<Content> content;
    
    @Data
    public static class Content {
        private String type;
        private String text;
    }
}
