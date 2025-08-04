package com.sorta.service.utils;

import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class SessionIdGenerator {
    
    public String generate() {
        return UUID.randomUUID().toString();
    }
}