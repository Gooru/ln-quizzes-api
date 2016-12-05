package com.quizzes.api.common.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

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
