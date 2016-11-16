package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.response.AttemptDto;

import java.util.List;
import java.util.UUID;

public class StartContextEventResponseDocDto {

    private UUID id;

    private CollectionDto collection;

    private UUID currentResourceId;

    private List<AttemptDto> attempt;


    public StartContextEventResponseDocDto() {
    }

    public StartContextEventResponseDocDto(UUID id, CollectionDto collection, UUID currentResourceId,
                                           List<AttemptDto> attempt) {
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

    public List<AttemptDto> getAttempt() {
        return attempt;
    }

    public void setAttempt(List<AttemptDto> attempt) {
        this.attempt = attempt;
    }

}
