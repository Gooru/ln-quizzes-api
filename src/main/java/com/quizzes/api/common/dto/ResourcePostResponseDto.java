package com.quizzes.api.common.dto;

import java.util.UUID;

public class ResourcePostResponseDto extends ResourceCommonDto {
    private int score;

    private UUID resourceId;

    public ResourcePostResponseDto() {
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
