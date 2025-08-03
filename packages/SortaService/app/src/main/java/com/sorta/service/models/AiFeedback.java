package com.sorta.service.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiFeedback {
    private SortaAgentPlan suggestions;
    private String generatedAt;
}