package com.quizzes.api.common.dto.api;

import com.google.gson.annotations.SerializedName;

public class TokenDTO {

    @SerializedName("access_token")
    private String token;

    public TokenDTO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
