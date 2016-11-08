package com.quizzes.api.common.dto;

import java.util.UUID;

public class ContextIdResponseDto {
    private UUID id;

    public ContextIdResponseDto(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
