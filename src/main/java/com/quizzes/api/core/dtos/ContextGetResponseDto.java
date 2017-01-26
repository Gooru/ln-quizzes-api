package com.quizzes.api.core.dtos;

import java.util.List;

public class ContextGetResponseDto extends CommonContextGetResponseDto {

    private IdResponseDto owner;

    private List<IdResponseDto> assignees;

    public ContextGetResponseDto() {
    }

    public IdResponseDto getOwner() {
        return owner;
    }

    public void setOwner(IdResponseDto owner) {
        this.owner = owner;
    }

    public List<IdResponseDto> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<IdResponseDto> assignees) {
        this.assignees = assignees;
    }

}
