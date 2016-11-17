package com.quizzes.api.common.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

public class CreatedContextGetResponseDto extends CommonContextGetResponseDto {

    private List<IdResponseDto> assignees;

    @ApiModelProperty(hidden = true)
    @SerializedName("contextData")
    private Map<String, Object> contextResponse;

    public CreatedContextGetResponseDto() {
    }

    public List<IdResponseDto> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<IdResponseDto> assignees) {
        this.assignees = assignees;
    }

    public Map<String, Object> getContextResponse() {
        return contextResponse;
    }

    public void setContextResponse(Map<String, Object> contextResponse) {
        this.contextResponse = contextResponse;
    }
}
