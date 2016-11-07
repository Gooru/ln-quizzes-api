package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDTO;

import java.util.List;

public class ContextGetResponseDto extends CommonContextGetResponseDto {

    private ProfileDTO owner;

    private List<ProfileDTO> assignees;

    public ContextGetResponseDto() {
    }

    public ProfileDTO getOwner() {
        return owner;
    }

    public void setOwner(ProfileDTO owner) {
        this.owner = owner;
    }

    public List<ProfileDTO> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDTO> assignees) {
        this.assignees = assignees;
    }
}
