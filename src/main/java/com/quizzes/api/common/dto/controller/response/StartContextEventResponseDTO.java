package com.quizzes.api.common.dto.controller.response;

import com.quizzes.api.common.dto.controller.CollectionDTO;

import java.util.List;
import java.util.UUID;

public class StartContextEventResponseDTO {

    private UUID id;

    private CollectionDTO collection;

    private UUID currentResourceId;

    private List<AttemptDTO> attempt;


    public StartContextEventResponseDTO() {
    }

    public StartContextEventResponseDTO(UUID id, CollectionDTO collection, UUID currentResourceId,
                                        List<AttemptDTO> attempt) {
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

    public CollectionDTO getCollection() {
        return collection;
    }

    public void setCollection(CollectionDTO collection) {
        this.collection = collection;
    }

    public UUID getCurrentResourceId() {
        return currentResourceId;
    }

    public void setCurrentResourceId(UUID currentResourceId) {
        this.currentResourceId = currentResourceId;
    }

    public List<AttemptDTO> getAttempt() {
        return attempt;
    }

    public void setAttempt(List<AttemptDTO> attempt) {
        this.attempt = attempt;
    }

}
