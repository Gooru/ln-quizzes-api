package com.quizzes.api.core.dtos;

import com.quizzes.api.core.dtos.controller.CollectionDto;

import java.util.List;
import java.util.UUID;

public class StartContextEventResponseDto {

    private UUID contextId;

    private UUID collectionId;

    private UUID currentResourceId;

    private List<PostResponseResourceDto> events;

    public StartContextEventResponseDto() {
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

    public UUID getCurrentResourceId() {
        return currentResourceId;
    }

    public void setCurrentResourceId(UUID currentResourceId) {
        this.currentResourceId = currentResourceId;
    }

    public List<PostResponseResourceDto> getEvents() {
        return events;
    }

    public void setEvents(List<PostResponseResourceDto> events) {
        this.events = events;
    }
}