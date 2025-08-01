package com.sorta.service.workflow;

import com.sorta.service.models.SortaAgentRequest;

public interface AgentMessageAugmentationWorkflow {
    String run(SortaAgentRequest request, String message);
    Boolean shouldRun(SortaAgentRequest request);
}
