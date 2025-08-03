package com.sorta.service.models.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortaAgentRequest {
    String message;
    String userId;
    String sessionId;
    List<String> images;
}
