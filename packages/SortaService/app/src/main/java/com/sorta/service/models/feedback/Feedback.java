package com.sorta.service.models.feedback;

import com.sorta.service.models.agent.SortaAgentPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    private SortaAgentPlan suggestions;
    private String generatedAt;
}