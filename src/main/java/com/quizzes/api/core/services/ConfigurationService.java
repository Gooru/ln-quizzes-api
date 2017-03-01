package com.quizzes.api.core.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Value("${content.api.client_key}")
    private String clientKey;

    @Value("${content.api.client_id}")
    private String clientId;

    @Value("${content.api.api_key}")
    private String apiKey;

    public String getContentApiUrl() {
        return contentApiUrl;
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getClientId() {
        return clientId;
    }

    public String getApiKey() {
        return apiKey;
    }

}
