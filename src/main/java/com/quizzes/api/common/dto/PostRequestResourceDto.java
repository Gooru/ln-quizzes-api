package com.quizzes.api.common.dto;

import io.swagger.annotations.ApiModelProperty;

public class PostRequestResourceDto extends CommonResourceDto {

    /* These property are only used to save that field in the database */
    @ApiModelProperty(hidden = true)
    private int score = 0;

    @ApiModelProperty(hidden = true)
    private boolean isSkipped = true;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean getIsSkipped() {
        return isSkipped;
    }

    public void setIsSkipped(boolean skipped) {
        isSkipped = skipped;
    }
}
