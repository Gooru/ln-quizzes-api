package com.quizzes.api.content.gooru.dto;

import com.google.gson.annotations.SerializedName;

public class TokenUserRequestDto {

    @SerializedName("client_key")
    private String clientKey;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("grant_type")
    private String grantType;

    @SerializedName("return_url")
    private String returnUrl;

    private UserDataTokenDto user;

    public TokenUserRequestDto(String clientKey, String clientId, String grantType, String returnUrl, UserDataTokenDto user) {
        this.clientKey = clientKey;
        this.clientId = clientId;
        this.grantType = grantType;
        this.returnUrl = returnUrl;
        this.user = user;
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

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public UserDataTokenDto getUser() {
        return user;
    }

    public void setUser(UserDataTokenDto user) {
        this.user = user;
    }
}
