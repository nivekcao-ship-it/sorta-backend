package com.sorta.service.models;

import lombok.Data;

import java.util.List;

@Data
public class BedrockFMResponse {
    private List<Content> content;
    
    @Data
    public static class Content {
        private String type;
        private String text;
    }
}