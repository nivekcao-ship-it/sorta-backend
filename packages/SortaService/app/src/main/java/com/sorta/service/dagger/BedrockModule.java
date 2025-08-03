package com.sorta.service.dagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.sorta.service.converters.JsonSchemaConverter;
import com.sorta.service.models.SortaAgentResponse;
import com.sorta.service.workflow.AgentMessageAugmentationWorkflow;
import com.sorta.service.workflow.ImageInputWorkflow;
import com.sorta.service.workflow.ResponseSchemaWorkflow;
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
public class BedrockModule {
    
//    private static final String AGENT_RESPONSE_SCHEMA;
//
//    static {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonSchemaConverter converter = new JsonSchemaConverter(objectMapper);
//            AGENT_RESPONSE_SCHEMA = converter.convertToSchema(SortaAgentResponse.class).toPrettyString();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate schema during class loading", e);
//        }
//    }

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
    @Named("sorta.bedrock.responseSchema")
    public String provideAgentResponseSchema() {
        return """
        {
          "type": "object",
          "properties": {
            "sessionId": {
              "type": "string"
            },
            "userId": {
              "type": "string"
            },
            "message": {
              "type": "object",
              "properties": {
                "text": {
                  "type": "string"
                },
                "data": {
                  "type": "object",
                  "properties": {
                    "plan": {
                      "type": "object",
                      "properties": {
                        "itemPlans": {
                          "type": "array",
                          "items": {
                            "type": "object",
                            "properties": {
                              "itemId": {
                                "type": "string"
                              },
                              "name": {
                                "type": "string"
                              },
                              "coordinates": {
                                "type": "object",
                                "properties": {
                                  "x": {
                                    "type": "number"
                                  },
                                  "y": {
                                    "type": "number"
                                  },
                                  "width": {
                                    "type": "number"
                                  },
                                  "height": {
                                    "type": "number"
                                  }
                                }
                              },
                              "suggestedAction": {
                                "type": "string",
                                "enum": ["KEEP", "DISCARD", "RELOCATE"]
                              },
                              "suggestedLocation": {
                                "type": "string"
                              },
                              "reason": {
                                "type": "string"
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            },
            "success": {
              "type": "boolean"
            },
            "timestamp": {
              "type": "string"
            }
          }
        }
        """;
    }

    @Provides
    @Singleton
    public List<AgentMessageAugmentationWorkflow> provideMessageAugmentationWorkflow(final ObjectMapper objectMapper,
                                                                                     @Named("sorta.bedrock.responseSchema") final String schema) {
        return ImmutableList.of(
                new ImageInputWorkflow(objectMapper),
                new ResponseSchemaWorkflow(schema));
    }
}
