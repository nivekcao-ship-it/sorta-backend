package com.sorta.service.models.feedback;

import com.sorta.service.models.agent.SortaAgentPlan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feedback {
    private SortaAgentPlan suggestions;
    private String generatedAt;
}