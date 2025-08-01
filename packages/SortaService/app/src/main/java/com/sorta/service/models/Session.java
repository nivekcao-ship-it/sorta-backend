package com.sorta.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Session {
    private String sessionId;
    private String userId;
    private String roomId;
    private String roomName;
    private String status;
    private String createdAt;
    private String completedAt;
    private SessionPhoto beforePhoto;
    private SessionPhoto afterPhoto;
    private AiFeedback aiFeedback;

    @DynamoDbPartitionKey
    public String getSessionId() {
        return sessionId;
    }

    @DynamoDbSortKey
    public String getUserId() {
        return userId;
    }
}