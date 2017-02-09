package com.quizzes.api.core.dtos;

import java.util.List;
import java.util.UUID;

public class ContextAttemptsResponseDto {

    private UUID contextId;
    private UUID collectionId;
    private List<ProfileAttemptsResponseDto> profileAttempts;

    public ContextAttemptsResponseDto() {
    }

    public UUID getContextId() {
        return contextId;
    }

    public void setContextId(UUID contextId) {
        this.contextId = contextId;
    }

    public UUID getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(UUID collectionId) {
        this.collectionId = collectionId;
    }

    public List<ProfileAttemptsResponseDto> getProfileAttempts() {
        return profileAttempts;
    }

    public void setProfileAttempts(List<ProfileAttemptsResponseDto> profileAttempts) {
        this.profileAttempts = profileAttempts;
    }
}
