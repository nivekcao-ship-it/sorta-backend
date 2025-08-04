package com.sorta.service.workflow.agentchat;

import com.sorta.service.models.agent.SortaAgentRequest;
import com.sorta.service.workflow.Workflow;

public interface AgentMessageAugmentationWorkflow extends Workflow<SortaAgentRequest, String, String> {
}