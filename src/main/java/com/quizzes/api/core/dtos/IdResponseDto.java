package com.quizzes.api.core.dtos;

import java.util.UUID;

public class IdResponseDto {

    private UUID id;

    public IdResponseDto() {}

    public IdResponseDto(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
