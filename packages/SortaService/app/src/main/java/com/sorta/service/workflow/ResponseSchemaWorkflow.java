package com.sorta.service.workflow;

import com.sorta.service.models.agent.SortaAgentRequest;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ResponseSchemaWorkflow implements AgentMessageAugmentationWorkflow {
    private final String schema;

    public ResponseSchemaWorkflow(final String schema) {
        this.schema = schema;
    }

    @Override
    public String run(SortaAgentRequest request, String message) {
        if (!shouldRun(request)) {
            return message;
        }

        return message + String.format(
                "\n\n Please strictly structure your response in Json only following this schema: %s", schema);
    }

    @Override
    public Boolean shouldRun(SortaAgentRequest request) {
        return false;
    }
}
