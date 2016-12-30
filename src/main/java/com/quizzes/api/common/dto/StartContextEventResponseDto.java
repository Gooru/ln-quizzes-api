package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.CollectionDto;

import java.util.List;
import java.util.UUID;

public class StartContextEventResponseDto {

    private UUID id;

    private CollectionDto collection;

    private UUID currentResourceId;

    private List<PostResponseResourceDto> events;

    public StartContextEventResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CollectionDto getCollection() {
        return collection;
    }

    public void setCollection(CollectionDto collection) {
        this.collection = collection;
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