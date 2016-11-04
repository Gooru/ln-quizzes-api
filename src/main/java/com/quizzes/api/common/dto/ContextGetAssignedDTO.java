package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDTO;

public class ContextGetAssignedDTO extends CommonContextGetDTO {

    private ProfileDTO owner;

    public ContextGetAssignedDTO() {
    }

    public ProfileDTO getOwner() {
        return owner;
    }

    public void setOwner(ProfileDTO owner) {
        this.owner = owner;
    }
}
