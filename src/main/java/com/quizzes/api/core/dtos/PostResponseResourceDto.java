package com.quizzes.api.core.dtos;

public class PostResponseResourceDto extends CommonResourceDto {

    private int score;
    private boolean isSkipped;

    public PostResponseResourceDto() {
    }

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
