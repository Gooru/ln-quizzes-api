package com.quizzes.api.core.dtos;

import java.util.List;
import java.util.UUID;

public class AttemptIdsResponseDto {
    private List<UUID> attempts;

    public List<UUID> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<UUID> attempts) {
        this.attempts = attempts;
    }
}
