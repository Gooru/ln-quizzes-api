package com.quizzes.api.common.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;

public class ResourcePostRequestDto extends ResourceCommonDto {

    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    /* This property is only used to save that field in the database */
    @ApiModelProperty(hidden = true)
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
