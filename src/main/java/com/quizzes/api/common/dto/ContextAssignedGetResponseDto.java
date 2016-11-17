package com.quizzes.api.common.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

public class ContextAssignedGetResponseDto extends CommonContextGetResponseDto {

    private IdResponseDto owner;

    @ApiModelProperty(hidden = true)
    @SerializedName("contextData")
    private Map<String, Object> contextDataResponse;

    public ContextAssignedGetResponseDto() {
    }

    public IdResponseDto getOwner() {
        return owner;
    }

    public void setOwner(IdResponseDto owner) {
        this.owner = owner;
    }

    public Map<String, Object> getContextDataResponse() {
        return contextDataResponse;
    }

    public void setContextDataResponse(Map<String, Object> contextDataResponse) {
        this.contextDataResponse = contextDataResponse;
    }
}
