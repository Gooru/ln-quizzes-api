package com.quizzes.api.core.dtos;

public class SessionPostRequestDto {

    private String clientApiKey;
    private String clientApiSecret;
    private ExternalUserDto user;

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

    public ExternalUserDto getUser() {
        return user;
    }

    public void setUser(ExternalUserDto user) {
        this.user = user;
    }
}
