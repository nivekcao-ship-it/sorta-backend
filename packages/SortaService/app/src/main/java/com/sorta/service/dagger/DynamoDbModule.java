package com.sorta.service.dagger;

import com.sorta.service.exceptions.InternalServerException;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class DynamoDbModule {

    @Provides
    @Singleton
    public DynamoDbClient provideDynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    @Provides
    @Singleton
    public DynamoDbEnhancedClient provideDynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Provides
    @Named("sorta.dynamodb.usersTableName")
    public String provideUsersTableName() {
        final String usersTableName = System.getenv("USERS_TABLE_NAME");
        if (usersTableName == null) {
            throw new InternalServerException("Users table name shouldn't be null");
        }
        return usersTableName;
    }

    @Provides
    @Named("sorta.dynamodb.roomsTableName")
    public String provideRoomsTableName() {
        final String spacesTableName = System.getenv("ROOMS_TABLE_NAME");
        if (spacesTableName == null) {
            throw new InternalServerException("Spaces table name shouldn't be null");
        }
        return spacesTableName;
    }

    @Provides
    @Named("sorta.dynamodb.sessionsTableName")
    public String provideSessionsTableName() {
        final String sessionTableName =  System.getenv("SESSIONS_TABLE_NAME");
        if (sessionTableName == null) {
            throw new InternalServerException("Session table name shouldn't be null");
        }
        return sessionTableName;
    }
}