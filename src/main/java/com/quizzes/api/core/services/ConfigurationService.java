package com.quizzes.api.core.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConfigurationService {

    @Value("${content.api.url}")
    private String contentApiUrl;

    public String getContentApiUrl() {
        return contentApiUrl;
    }

}
