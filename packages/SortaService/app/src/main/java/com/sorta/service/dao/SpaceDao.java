package com.sorta.service.dao;

import com.sorta.service.models.Space;
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
public class SpaceDao {
    private final DynamoDbEnhancedClient dynamoDbClient;
    private final String spacesTableName;

    @Inject
    public SpaceDao(final DynamoDbEnhancedClient dynamoDbClient,
                    @Named("sorta.dynamodb.roomsTableName") final String spacesTableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.spacesTableName = spacesTableName;
    }

    private DynamoDbTable<Space> getSpaceTable() {
        return dynamoDbClient.table(spacesTableName, TableSchema.fromBean(Space.class));
    }

    public void saveSpace(final Space space) {
        getSpaceTable().putItem(space);
        log.info("Saved room: {} for user: {}", space.getSpaceId(), space.getUserId());
    }

    public Optional<Space> getSpace(final String userId, final String spaceId) {
        final Key key = Key.builder()
                .partitionValue(userId)
                .sortValue(spaceId)
                .build();
        
        final Space space = getSpaceTable().getItem(key);
        return Optional.ofNullable(space);
    }

    public List<Space> getSpacesByUser(final String userId) {
        final QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(userId).build()
        );
        
        return getSpaceTable().query(queryConditional)
                .items()
                .stream()
                .toList();
    }

    public void deleteSpace(final String userId, final String spaceId) {
        final Key key = Key.builder()
                .partitionValue(userId)
                .sortValue(spaceId)
                .build();
        
        getSpaceTable().deleteItem(key);
        log.info("Deleted room: {} for user: {}", spaceId, userId);
    }
}