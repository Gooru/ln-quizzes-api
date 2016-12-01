package com.quizzes.api.content.gooru.dto;

import com.google.gson.annotations.SerializedName;

public class TokenUserRequestDto extends CommonTokenDto {

    @SerializedName("return_url")
    private String returnUrl;

    private UserDataTokenDto user;

    public TokenUserRequestDto() {
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
