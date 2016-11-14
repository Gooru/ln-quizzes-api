package com.quizzes.api.common.dto;

import com.google.gson.annotations.SerializedName;
import com.quizzes.api.common.dto.controller.ProfileDto;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

public class ContextAssignedGetResponseDto extends CommonContextGetResponseDto {

    private transient ProfileDto owner;

    @ApiModelProperty(hidden = true)
    @SerializedName("owner")
    private Map<String, Object> ownerResponse;

    @ApiModelProperty(hidden = true)
    @SerializedName("contextData")
    private Map<String, Object> contextResponse;

    public ContextAssignedGetResponseDto() {
    }

    public ProfileDto getOwner() {
        return owner;
    }

    public void setOwner(ProfileDto owner) {
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
