package com.quizzes.api.core.dtos;

import java.util.UUID;

public class SessionTokenDto {

    private UUID sessionToken;

    public SessionTokenDto() {
    }

    public UUID getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(UUID sessionToken) {
        this.sessionToken = sessionToken;
    }
}
