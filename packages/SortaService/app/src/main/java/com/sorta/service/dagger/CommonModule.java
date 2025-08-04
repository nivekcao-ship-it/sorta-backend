package com.sorta.service.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorta.service.utils.SessionIdGenerator;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class CommonModule {

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    public SessionIdGenerator provideSessionIdGenerator() {
        return new SessionIdGenerator();
    }
}
