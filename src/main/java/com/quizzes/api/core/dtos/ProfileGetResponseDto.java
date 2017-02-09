package com.quizzes.api.core.dtos;

public class ProfileGetResponseDto extends ProfileDto {
    private String externalId;

    public ProfileGetResponseDto() {
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
