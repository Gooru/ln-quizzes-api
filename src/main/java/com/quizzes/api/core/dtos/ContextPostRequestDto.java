package com.quizzes.api.core.dtos;

import com.quizzes.api.core.dtos.controller.ContextDataDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * This class is used to get the specific body context (json) in the endpoints
 */
public class ContextPostRequestDto {

    @NotNull(message = "{assignment.collectionId.not_null}")
    @Valid
    private String collectionId;

    private String classId;

    @NotNull(message = "{assignment.context_data.not_null}")
    private ContextDataDto contextData;

    public ContextPostRequestDto() {
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public ContextDataDto getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDto contextData) {
        this.contextData = contextData;
    }

}
