package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDto;

import java.util.List;

public class CreatedContextGetResponseDto extends CommonContextGetResponseDto {

    private List<ProfileDto> assignees;

    public CreatedContextGetResponseDto() {
    }

    public List<ProfileDto> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDto> assignees) {
        this.assignees = assignees;
    }
}
