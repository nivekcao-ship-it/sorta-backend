package com.sorta.service.dagger;

import com.google.common.collect.ImmutableList;
import com.sorta.service.workflow.AgentMessageAugmentationWorkflow;
import com.sorta.service.workflow.ImageInputWorkflow;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Module
public class AgentModule {

    @Provides
    @Singleton
    public BedrockRuntimeClient provideBedrockRuntimeClient() {
        return BedrockRuntimeClient.builder()
                .region(Region.US_WEST_2) // Default region, can be overridden by AWS_REGION env var
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();
    }

    @Provides
    @Singleton
    public BedrockAgentRuntimeAsyncClient provideBedrockAgentClient() {
        return BedrockAgentRuntimeAsyncClient.builder()
                .region(Region.US_WEST_2) // Default region, can be overridden by AWS_REGION env var
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    @Provides
    @Singleton
    @Named("sorta.bedrock.agentId")
    public String provideAgentId() {
        final String agentId = System.getenv("BEDROCK_AGENT_ID");
        if (agentId == null) {
            throw new InternalError("Agent Id shouldn't be null");
        }
        return agentId;
    }

    @Provides
    @Singleton
    @Named("sorta.bedrock.agentAlias")
    public String provideAgentAlias() {
        final String agentAlias = System.getenv("BEDROCK_AGENT_ALIAS_ID");
        if (agentAlias == null) {
            throw new InternalError("Agent alias shouldn't be null");
        }
        return agentAlias;
    }

    @Provides
    @Singleton
    public List<AgentMessageAugmentationWorkflow> provideMessageAugmentationWorkflow() {
        return ImmutableList.of(new ImageInputWorkflow());
    }
}
