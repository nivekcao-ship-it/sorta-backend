package com.sorta.service.workflow.agentchat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.exceptions.InternalServerException;
import com.sorta.service.models.agent.SortaAgentRequest;
import com.sorta.service.workflow.Workflow;

public class ImageInputWorkflow implements AgentMessageAugmentationWorkflow {
    private final ObjectMapper objectMapper;

    public ImageInputWorkflow(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String run(final SortaAgentRequest request, final String message) {
        if (!shouldRun(request)) {
            return message;
        }

        try {
            final String json = this.objectMapper.writeValueAsString(request.getImages());
            return message + String.format("\n Following images are provided: %s", json);
        } catch (final Exception e) {
            throw new InternalServerException("error running image input workflow", e);
        }
    }

    @Override
    public Boolean shouldRun(final SortaAgentRequest request) {
        return request.getImages() != null && !request.getImages().isEmpty();
    }
}
