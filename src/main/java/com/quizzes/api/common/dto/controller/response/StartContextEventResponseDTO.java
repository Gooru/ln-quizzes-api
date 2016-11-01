package com.quizzes.api.common.dto.controller.response;

import java.util.List;
import java.util.UUID;

public class StartContextEventResponseDTO {
    private UUID currentResourceId;
    private List<AttemptDTO> attempt;

    public StartContextEventResponseDTO(UUID currentResourceId, List<AttemptDTO> attempt) {
        this.currentResourceId = currentResourceId;
        this.attempt = attempt;
    }

    public UUID getCurrentResourceId() {
        return currentResourceId;
    }

    public List<AttemptDTO> getAttempt() {
        return attempt;
    }
}
