package com.quizzes.api.common.dto.controller.response;

import java.util.UUID;

public class CollectionDataResourceDto {
    UUID id;
    boolean isResource;
    QuestionDataDto questionData;

    public CollectionDataResourceDto(UUID id, boolean isResource, QuestionDataDto questionData) {
        this.id = id;
        this.isResource = isResource;
        this.questionData = questionData;
    }

    public UUID getId() {
        return id;
    }

    public boolean getIsResource() {
        return isResource;
    }

    public QuestionDataDto getQuestionData() {
        return questionData;
    }
}
