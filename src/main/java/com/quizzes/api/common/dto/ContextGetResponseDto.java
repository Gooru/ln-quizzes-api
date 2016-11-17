package com.quizzes.api.common.dto;

import com.google.gson.annotations.SerializedName;
import com.quizzes.api.common.dto.controller.ProfileDto;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

public class ContextGetResponseDto extends CommonContextGetResponseDto {

    private transient ProfileDto owner;

    @ApiModelProperty(hidden = true)
    @SerializedName("owner")
    private Map<String, Object> ownerResponse;

    private transient List<ProfileDto> assignees;

    @ApiModelProperty(hidden = true)
    @SerializedName("assignees")
    private List<Map<String, Object>> assigneesResponse;

    @ApiModelProperty(hidden = true)
    @SerializedName("contextData")
    private Map<String, Object> contextDataResponse;

    public ContextGetResponseDto() {
    }

    public ProfileDto getOwner() {
        return owner;
    }

    public List<ProfileDto> getAssignees() {
        return assignees;
    }

    public Map<String, Object> getOwnerResponse() {
        return ownerResponse;
    }

    public void setOwnerResponse(Map<String, Object> ownerResponse) {
        this.ownerResponse = ownerResponse;
    }

    public List<Map<String, Object>> getAssigneesResponse() {
        return assigneesResponse;
    }

    public void setAssigneesResponse(List<Map<String, Object>> assigneesResponse) {
        this.assigneesResponse = assigneesResponse;
    }

    public Map<String, Object> getContextDataResponse() {
        return contextDataResponse;
    }

    public void setContextDataResponse(Map<String, Object> contextDataResponse) {
        this.contextDataResponse = contextDataResponse;
    }
}
