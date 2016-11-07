package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDTO;

public class ContextGetAssignedResponseDto extends CommonContextGetResponseDto {

    private ProfileDTO owner;

    public ContextGetAssignedResponseDto() {
    }

    public ProfileDTO getOwner() {
        return owner;
    }

    public void setOwner(ProfileDTO owner) {
        this.owner = owner;
    }
}
