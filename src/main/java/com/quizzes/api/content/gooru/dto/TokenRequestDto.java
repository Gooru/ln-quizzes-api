package com.quizzes.api.content.gooru.dto;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class TokenRequestDto {

    @SerializedName("client_key")
    private String clientKey;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("grant_type")
    private String grantType;

    public TokenRequestDto() {
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

}