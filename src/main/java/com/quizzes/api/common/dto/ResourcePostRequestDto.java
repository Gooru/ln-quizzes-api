package com.quizzes.api.common.dto;

import io.swagger.annotations.ApiModelProperty;

public class ResourcePostRequestDto extends ResourceCommonDto {

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
