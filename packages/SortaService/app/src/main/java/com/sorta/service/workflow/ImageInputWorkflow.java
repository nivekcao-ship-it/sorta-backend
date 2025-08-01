package com.sorta.service.workflow;

import com.sorta.service.models.SortaAgentRequest;

public class ImageInputWorkflow implements  AgentMessageAugmentationWorkflow {

    @Override
    public String run(final SortaAgentRequest request, final String message) {
        if (!shouldRun(request)) {
            return message;
        }

        return message + String.format("\n Following images are provided: %s", request.getImages());
    }

    @Override
    public Boolean shouldRun(final SortaAgentRequest request) {
        return request.getImages() != null && !request.getImages().isEmpty();
    }
}
