package com.quizzes.api.core.dtos;

import java.util.List;

public class AttemptIdsResponseDto {
    private List<IdResponseDto> attempts;

    public List<IdResponseDto> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<IdResponseDto> attempts) {
        this.attempts = attempts;
    }
}
