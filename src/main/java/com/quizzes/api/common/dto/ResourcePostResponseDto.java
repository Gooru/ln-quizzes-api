package com.quizzes.api.common.dto;

public class ResourcePostResponseDto extends ResourceCommonDto {
    private int score;

    public ResourcePostResponseDto() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
