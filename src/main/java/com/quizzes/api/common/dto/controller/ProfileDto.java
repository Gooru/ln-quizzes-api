package com.quizzes.api.common.dto.controller;

import com.quizzes.api.common.dto.CommonProfileDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProfileDto extends CommonProfileDto {

    @NotNull(message = "{profile.id.not_null}")
    @Size(min = 1, message = "{profile.id.size}")
    private String id;

    public ProfileDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
