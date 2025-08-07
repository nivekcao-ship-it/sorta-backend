package com.sorta.service.models.agentactiongroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambdaActionGroupResponse {
    private String messageVersion;
    private Response response;
    private Map<String, String> sessionAttributes;
    private Map<String, String> promptSessionAttributes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String actionGroup;
        private String apiPath;
        private String httpMethod;
        private Integer httpStatusCode;
        private Map<String, ResponseContent> responseBody;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseContent {
        private String body;
    }
}