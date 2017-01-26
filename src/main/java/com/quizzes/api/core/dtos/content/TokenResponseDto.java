package com.quizzes.api.content.dtos;

import com.google.gson.annotations.SerializedName;

public class TokenResponseDto {

    @SerializedName("access_token")
    private String token;

    public TokenResponseDto() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
