package com.quizzes.api.core.dtos;

import com.quizzes.api.core.dtos.controller.CollectionDto;

import java.util.List;
import java.util.UUID;

public class ContextEventsResponseDto {

    private UUID contextId;
    private CollectionDto collection;
    private List<ProfileEventResponseDto> profileEvents;

    public ContextEventsResponseDto() {
    }

    public UUID getContextId() {
        return contextId;
    }

    public void setContextId(UUID contextId) {
        this.contextId = contextId;
    }

    public CollectionDto getCollection() {
        return collection;
    }

    public void setCollection(CollectionDto collection) {
        this.collection = collection;
    }

    public List<ProfileEventResponseDto> getProfileEvents() {
        return profileEvents;
    }

    public void setProfileEvents(List<ProfileEventResponseDto> profileEvents) {
        this.profileEvents = profileEvents;
    }
}
