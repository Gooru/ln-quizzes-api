package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.ProfileDTO;

import java.util.List;

public class ContextGetCreatedDTO extends CommonContextGetDTO {

    private List<ProfileDTO> assignees;

    public ContextGetCreatedDTO() {
    }

    public List<ProfileDTO> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<ProfileDTO> assignees) {
        this.assignees = assignees;
    }
}
