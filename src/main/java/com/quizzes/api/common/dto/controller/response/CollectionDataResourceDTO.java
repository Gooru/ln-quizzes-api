package com.quizzes.api.common.dto.controller.response;

import java.util.UUID;

public class CollectionDataResourceDTO {
    UUID id;
    boolean isResource;
    QuestionDataDTO questionData;

    public CollectionDataResourceDTO(UUID id, boolean isResource, QuestionDataDTO questionData) {
        this.id = id;
        this.isResource = isResource;
        this.questionData = questionData;
    }

    public UUID getId() {
        return id;
    }

    public boolean isResource() {
        return isResource;
    }

    public QuestionDataDTO getQuestionData() {
        return questionData;
    }
}
