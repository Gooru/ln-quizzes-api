package com.quizzes.api.core.dtos;

import com.quizzes.api.core.dtos.controller.ContextDataDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * This class is used to get the specific body context (json) in the endpoints
 */
public class ContextPostRequestDto {

    @NotNull(message = "{assignment.collectionId.not_null}")
    @Valid
    private UUID collectionId;

    private UUID classId;

    private boolean isCollection;

    @NotNull(message = "{assignment.context_data.not_null}")
    private ContextDataDto contextData;

    public ContextPostRequestDto() {
    }

    public UUID getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(UUID collectionId) {
        this.collectionId = collectionId;
    }

    public UUID getClassId() {
        return classId;
    }

    public void setClassId(UUID classId) {
        this.classId = classId;
    }

    public ContextDataDto getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDto contextData) {
        this.contextData = contextData;
    }

    public boolean getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(boolean collection) {
        isCollection = collection;
    }
}
