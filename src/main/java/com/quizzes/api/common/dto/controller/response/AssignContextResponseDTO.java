package com.quizzes.api.common.dto.controller.response;

import java.util.UUID;

public class AssignContextResponseDTO {
    private UUID id;

    public AssignContextResponseDTO(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
