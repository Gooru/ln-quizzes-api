package com.quizzes.api.common.dto.api;

public class TokenDTO {

    private String access_token;

    public TokenDTO() {
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
