package com.quizzes.api.core.dtos.messaging;

import java.util.UUID;

public class StartContextEventMessageDto {

    private boolean isNewAttempt;
    private UUID currentResourceId;

    public boolean isNewAttempt() {
        return isNewAttempt;
    }

    public void setIsNewAttempt(boolean isNewAttempt) {
        this.isNewAttempt = isNewAttempt;
    }

    public UUID getCurrentResourceId() {
        return currentResourceId;
    }

    public void setCurrentResourceId(UUID currentResourceId) {
        this.currentResourceId = currentResourceId;
    }

}
