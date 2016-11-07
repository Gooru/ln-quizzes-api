package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDTO;

import java.util.List;

public class ContextGetCreatedResponseResponseDto extends CommonContextGetResponseDto {

    private List<ProfileDTO> assignees;

    public ContextGetCreatedResponseResponseDto() {
    }

    public List<ProfileDTO> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDTO> assignees) {
        this.assignees = assignees;
    }
}
