package com.sorta.service.models.agent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SortaAgentRequest {
    @JsonProperty(required = true)
    String message;
    
    @JsonProperty(required = true)
    String userId;
    
    String sessionId;
    List<String> images;
}
