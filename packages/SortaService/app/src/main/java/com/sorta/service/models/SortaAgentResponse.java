package com.sorta.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class SortaAgentResponse {
    String sessionId;
    String userId;
    SortaAgentMessage message;
    boolean success;
    String timestamp;
}
