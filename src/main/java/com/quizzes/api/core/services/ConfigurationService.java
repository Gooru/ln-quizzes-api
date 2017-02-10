package com.quizzes.api.core.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConfigurationService {

    public static final String ANONYMOUS = "anonymous";
    private static final UUID ANONYMOUS_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Value("${content.api.url}")
    private String contentApiUrl;

    public String getContentApiUrl() {
        return contentApiUrl;
    }

    public UUID getAnonymousId() {
        return ANONYMOUS_ID;
    }

}
