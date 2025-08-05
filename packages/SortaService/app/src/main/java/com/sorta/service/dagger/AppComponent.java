package com.sorta.service.dagger;

import com.sorta.service.handlers.internal.GetUserProfileHandler;
import com.sorta.service.handlers.SortaAgentActionGroupHandler;
import com.sorta.service.handlers.SortaServiceHandler;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {S3Module.class, BedrockModule.class, CommonModule.class, DynamoDbModule.class, UserModule.class})
public interface AppComponent {
    void inject(SortaAgentActionGroupHandler handler);
    void inject(SortaServiceHandler handler);
    void inject(GetUserProfileHandler handler);
    
    @Component.Builder
    interface Builder {
        AppComponent build();
    }
    
    static AppComponent getInstance() {
        return DaggerAppComponent.builder().build();
    }
}