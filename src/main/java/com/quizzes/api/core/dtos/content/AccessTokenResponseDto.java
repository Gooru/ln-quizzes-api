package com.quizzes.api.core.dtos.content;


import com.google.gson.annotations.SerializedName;

public class AccessTokenResponseDto {

    @SerializedName("user_id")
    String userId;

    @SerializedName("client_id")
    String clientId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
