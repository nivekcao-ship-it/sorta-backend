package com.sorta.service.dao;

import com.sorta.service.models.db.Session;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Log4j2
@Singleton
public class SessionDao {
    private final DynamoDbEnhancedClient dynamoDbClient;
    private final String sessionsTableName;

    @Inject
    public SessionDao(DynamoDbEnhancedClient dynamoDbClient,
                     @Named("sorta.dynamodb.sessionsTableName") String sessionsTableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.sessionsTableName = sessionsTableName;
    }

    private DynamoDbTable<Session> getSessionTable() {
        return dynamoDbClient.table(sessionsTableName, TableSchema.fromBean(Session.class));
    }

    public void saveSession(final Session session) {
        getSessionTable().putItem(session);
        log.info("Saved session: {} for user: {}", session.getSessionId(), session.getUserId());
    }

    public Optional<Session> getSession(final String sessionId, final String userId) {
        final Key key = Key.builder()
                .partitionValue(sessionId)
                .sortValue(userId)
                .build();
        
        final Session session = getSessionTable().getItem(key);
        return Optional.ofNullable(session);
    }

    public List<Session> getSessionsByUser(final String userId) {
        final QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder().sortValue(userId).build()
        );
        
        return getSessionTable().query(queryConditional)
                .items()
                .stream()
                .toList();
    }

    public void deleteSession(final String sessionId, final String userId) {
        final Key key = Key.builder()
                .partitionValue(sessionId)
                .sortValue(userId)
                .build();
        
        getSessionTable().deleteItem(key);
        log.info("Deleted session: {} for user: {}", sessionId, userId);
    }
}