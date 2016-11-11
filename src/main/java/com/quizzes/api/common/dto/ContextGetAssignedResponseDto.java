package com.quizzes.api.common.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

public class ContextGetAssignedResponseDto extends CommonContextGetResponseDto {

    private transient ProfileDTO owner;

    @ApiModelProperty(hidden = true)
    @SerializedName("owner")
    private Map<String, Object> ownerResponse;

    @ApiModelProperty(hidden = true)
    @SerializedName("contextData")
    private Map<String, Object> contextResponse;

    public ContextGetAssignedResponseDto() {
    }

    public ProfileDTO getOwner() {
        return owner;
    }

    public void setOwner(ProfileDTO owner) {
        this.owner = owner;
    }

    public Map<String, Object> getOwnerResponse() {
        return ownerResponse;
    }

    public void setOwnerResponse(Map<String, Object> ownerResponse) {
        this.ownerResponse = ownerResponse;
    }

    public Map<String, Object> getContextResponse() {
        return contextResponse;
    }

    public void setContextResponse(Map<String, Object> contextResponse) {
        this.contextResponse = contextResponse;
    }
}
