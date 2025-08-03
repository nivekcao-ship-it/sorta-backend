package com.sorta.service.models.agent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = SortaAgentResponse.SortaAgentResponseBuilder.class)
public class SortaAgentResponse {
    String sessionId;
    String userId;
    SortaAgentMessage message;
    boolean success;
    String timestamp;
}
