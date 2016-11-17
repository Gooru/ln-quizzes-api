package com.quizzes.api.common.dto.controller.response;

import java.util.UUID;

public class CollectionDataResourceDto {

    private UUID id;
    private boolean isResource;
    private int sequence;
    private QuestionDataDto questionData;

    public CollectionDataResourceDto(UUID id, boolean isResource, int sequence, QuestionDataDto questionData) {
        this.id = id;
        this.isResource = isResource;
        this.sequence = sequence;
        this.questionData = questionData;
    }

    public UUID getId() {
        return id;
    }

    // This method is required by Swagger to document correctly the property
    public boolean getIsResource() {
        return isResource;
    }

    public int getSequence() {
        return sequence;
    }

    public QuestionDataDto getQuestionData() {
        return questionData;
    }

}
