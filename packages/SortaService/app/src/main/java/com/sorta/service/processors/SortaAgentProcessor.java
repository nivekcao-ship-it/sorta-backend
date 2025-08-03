package com.sorta.service.processors;

import com.sorta.service.converters.SortaAgentConverter;
import com.sorta.service.exceptions.InternalServerException;
import com.sorta.service.models.SortaAgentRequest;
import com.sorta.service.models.SortaAgentResponse;
import com.sorta.service.workflow.AgentMessageAugmentationWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockagentruntime.model.InvokeAgentRequest;
import software.amazon.awssdk.services.bedrockagentruntime.model.InvokeAgentResponseHandler;
import software.amazon.awssdk.services.bedrockagentruntime.model.PayloadPart;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
@Log4j2
public class SortaAgentProcessor {
    private final BedrockAgentRuntimeAsyncClient bedrockAgentClient;
    private final SortaAgentConverter sortaAgentConverter;
    private final List<AgentMessageAugmentationWorkflow> workflows;
    private final String agentId;
    private final String agentAliasId;

    @Inject
    public SortaAgentProcessor(BedrockAgentRuntimeAsyncClient bedrockAgentClient,
                               SortaAgentConverter sortaAgentConverter,
                               List<AgentMessageAugmentationWorkflow> workflows,
                               @Named("sorta.bedrock.agentId") String agentId,
                               @Named("sorta.bedrock.agentAlias") String agentAliasId) {
        this.bedrockAgentClient = bedrockAgentClient;
        this.sortaAgentConverter = sortaAgentConverter;
        this.workflows = workflows;
        this.agentId = agentId;
        this.agentAliasId = agentAliasId;
    }

    public SortaAgentResponse process(final SortaAgentRequest request) {
        String augmentedMsg = request.getMessage();
        for (AgentMessageAugmentationWorkflow workflow: workflows) {
            augmentedMsg = workflow.run(request, augmentedMsg);
        }
        final InvokeAgentRequest invokeAgentRequest = InvokeAgentRequest.builder()
                .agentId(agentId)
                .agentAliasId(agentAliasId)
                .sessionId(request.getSessionId())
                .inputText(augmentedMsg)
                .build();

        try {
            log.info("Invoking agent - request: {}, message: {}", invokeAgentRequest, augmentedMsg);

            final StringBuilder responseText = new StringBuilder();

            final CompletableFuture<Void> future = bedrockAgentClient.invokeAgent(invokeAgentRequest,
                    InvokeAgentResponseHandler.builder()
                            .onEventStream(stream -> stream.subscribe(event -> {
                                if (event instanceof PayloadPart payloadPart) {
                                    if (payloadPart.bytes() != null) {
                                        responseText.append(payloadPart.bytes().asUtf8String());
                                    }
                                }
                            }))
                            .onError(throwable -> log.error("Error in stream: {}", throwable.getMessage()))
                            .build());

            future.get(); // Wait for completion

            log.info("Received response from agent: {}", responseText.toString());

            return sortaAgentConverter.toSortaAgentResponse(responseText.toString(), request.getSessionId(), request.getUserId());
        } catch (final Exception e) {
            log.error("Error invoking Bedrock agent: {}", e.getMessage(), e);
            throw new InternalServerException("Agent error", e);
        }
    }
}
