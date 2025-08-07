package com.sorta.service.models.agent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortaAgentResponse {
    String sessionId;
    String userId;
    SortaAgentMessage message;
    boolean success;
    String timestamp;
}
