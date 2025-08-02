package com.sorta.service.models;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class LambdaActionGroupResponse {
    private String messageVersion;
    private Response response;
    private Map<String, String> sessionAttributes;
    private Map<String, String> promptSessionAttributes;

    @Data
    @Builder
    public static class Response {
        private String actionGroup;
        private String apiPath;
        private String httpMethod;
        private Integer httpStatusCode;
        private Map<String, ResponseContent> responseBody;
    }

    @Data
    @Builder
    public static class ResponseContent {
        private String body;
    }
}