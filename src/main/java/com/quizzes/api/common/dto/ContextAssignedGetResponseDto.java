package com.quizzes.api.common.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

public class ContextAssignedGetResponseDto extends CommonContextGetResponseDto {

    private IdResponseDto owner;

    public ContextAssignedGetResponseDto() {
    }

    public IdResponseDto getOwner() {
        return owner;
    }

    public void setOwner(IdResponseDto owner) {
        this.owner = owner;
    }
}
