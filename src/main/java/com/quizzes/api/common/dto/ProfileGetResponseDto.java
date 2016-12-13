package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDto;

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
