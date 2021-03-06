package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;

public class UserTokenRequestDto extends TokenRequestDto {

    @SerializedName("return_url")
    private String returnUrl;

    private UserDataTokenDto user;

    public UserTokenRequestDto() {
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