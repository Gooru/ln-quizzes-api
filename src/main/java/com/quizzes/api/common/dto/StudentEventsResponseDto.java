package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.CollectionDto;

import java.util.List;
import java.util.UUID;

public class StudentEventsResponseDto {

    private UUID contextId;
    private CollectionDto collection;
    private List<ProfileEventResponseDto> profileEvents;

    public StudentEventsResponseDto() {
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
