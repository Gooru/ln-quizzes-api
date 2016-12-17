package com.quizzes.api.common.dto;

public class SessionPostRequestDto {

    private String clientApiKey;
    private String clientApiSecret;
    private ProfileDto profile;

    public SessionPostRequestDto() {
    }

    public String getClientApiKey() {
        return clientApiKey;
    }

    public void setClientApiKey(String clientApiKey) {
        this.clientApiKey = clientApiKey;
    }

    public String getClientApiSecret() {
        return clientApiSecret;
    }

    public void setClientApiSecret(String clientApiSecret) {
        this.clientApiSecret = clientApiSecret;
    }

    public ProfileDto getProfile() {
        return profile;
    }

    public void setProfile(ProfileDto profile) {
        this.profile = profile;
    }
}
