package com.sorta.service.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LambdaActionGroupRequest {
    private String messageVersion;
    private Agent agent;
    private String inputText;
    private String sessionId;
    private String actionGroup;
    private String apiPath;
    private String httpMethod;
    private List<Parameter> parameters;
    private RequestBody requestBody;
    private Map<String, String> sessionAttributes;
    private Map<String, String> promptSessionAttributes;

    @Data
    public static class Agent {
        private String name;
        private String id;
        private String alias;
        private String version;
    }

    @Data
    public static class Parameter {
        private String name;
        private String type;
        private String value;
    }

    @Data
    public static class RequestBody {
        private Map<String, ContentType> content;
    }

    @Data
    public static class ContentType {
        private List<Property> properties;
    }

    @Data
    public static class Property {
        private String name;
        private String type;
        private String value;
    }
}