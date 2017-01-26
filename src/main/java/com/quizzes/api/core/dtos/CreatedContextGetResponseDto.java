package com.quizzes.api.core.dtos;

import java.util.List;

public class CreatedContextGetResponseDto extends CommonContextGetResponseDto {

    private List<IdResponseDto> assignees;

    private long modifiedDate;

    public void setModifiedDate(long modifiedDate){
      this.modifiedDate = modifiedDate;
    }

    public long getModifiedDate(){
      return this.modifiedDate;
    }

    public CreatedContextGetResponseDto() {
    }

    public List<IdResponseDto> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<IdResponseDto> assignees) {
        this.assignees = assignees;
    }

}
