package com.sorta.service.dao;

import com.sorta.service.models.db.User;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Optional;

@Log4j2
@Singleton
public class UserDao {
    private final DynamoDbEnhancedClient dynamoDbClient;
    private final String usersTableName;

    @Inject
    public UserDao(DynamoDbEnhancedClient dynamoDbClient,
                   @Named("sorta.dynamodb.usersTableName") String usersTableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.usersTableName = usersTableName;
    }

    private DynamoDbTable<User> getUserTable() {
        return dynamoDbClient.table(usersTableName, TableSchema.fromBean(User.class));
    }

    public void saveUser(final User user) {
        getUserTable().putItem(user);
        log.info("Saved user: {}", user.getUserId());
    }

    public Optional<User> getUser(final String userId) {
        final Key key = Key.builder()
                .partitionValue(userId)
                .build();
        
        final User user = getUserTable().getItem(key);
        return Optional.ofNullable(user);
    }

    public void deleteUser(final String userId) {
        final Key key = Key.builder()
                .partitionValue(userId)
                .build();
        
        getUserTable().deleteItem(key);
        log.info("Deleted user: {}", userId);
    }

    public void updateUser(final User user) {
        getUserTable().updateItem(user);
        log.info("Updated user: {}", user.getUserId());
    }
}