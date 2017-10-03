package com.quizzes.api.core.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Value("${token.verification.api.url}")
    private String tokenVerificationUrl;

    @Value("${auth.signin.api.url}")
    private String signinUrl;

    @Value("${analytics.event.api.url}")
    private String analyticsEventApiUrl;

    @Value("${content.api.client_key}")
    private String clientKey;

    @Value("${content.api.client_id}")
    private String clientId;

    @Value("${content.api.api_key}")
    private String apiKey;

    @Value("${content.api.analytics_version}")
    private String analyticsVersion;

    @Value("${content.api.analytics.appId}")
    private String analyticsAppId;

    public String getContentApiUrl() {
        return contentApiUrl;
    }

    public String getTokenVerificationUrl() {
        return tokenVerificationUrl;
    }

    public String getSigninUrl() {
        return signinUrl;
    }

    public String getAnalyticsEventApiUrl() {
        return analyticsEventApiUrl;
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

    public String getAnalyticsVersion() {
        return analyticsVersion;
    }

    public String getAnalyticsAppId() {
        return analyticsAppId;
    }

}
