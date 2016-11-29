package com.quizzes.api.common.dto;

public class PostResponseResourceDto extends CommonResourceDto {
    private int score;

    public PostResponseResourceDto() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
