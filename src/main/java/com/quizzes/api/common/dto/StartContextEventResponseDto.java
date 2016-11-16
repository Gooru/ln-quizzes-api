package com.quizzes.api.common.dto.controller.response;

import com.quizzes.api.common.dto.controller.CollectionDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StartContextEventResponseDto {

    private UUID id;

    private CollectionDto collection;

    private UUID currentResourceId;

    private List<Map<String, Object>> attempt;


    public StartContextEventResponseDto() {
    }

    public StartContextEventResponseDto(UUID id, CollectionDto collection, UUID currentResourceId,
                                        List<Map<String, Object>> attempt) {
        this.id = id;
        this.collection = collection;
        this.currentResourceId = currentResourceId;
        this.attempt = attempt;
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

    public List<Map<String, Object>> getAttempt() {
        return attempt;
    }

    public void setAttempt(List<Map<String, Object>> attempt) {
        this.attempt = attempt;
    }

}