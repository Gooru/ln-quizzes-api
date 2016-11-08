package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDTO;

import java.util.List;

public class ContextGetCreatedResponseDto extends CommonContextGetResponseDto {

    private List<ProfileDTO> assignees;

    public ContextGetCreatedResponseDto() {
    }

    public List<ProfileDTO> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDTO> assignees) {
        this.assignees = assignees;
    }
}
