package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDto;

import java.util.List;

public class ContextGetResponseDto extends CommonContextGetResponseDto {

    private ProfileDto owner;

    private List<ProfileDto> assignees;

    public ContextGetResponseDto() {
    }

    public ProfileDto getOwner() {
        return owner;
    }

    public void setOwner(ProfileDto owner) {
        this.owner = owner;
    }

    public List<ProfileDto> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDto> assignees) {
        this.assignees = assignees;
    }
}
