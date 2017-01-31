package com.quizzes.api.core.dtos;

import java.util.UUID;

public class ResourceDto {

    private UUID id;

    private boolean isResource;

    private int sequence;

    private QuestionMetadataDto metadata;

    public ResourceDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean getIsResource() {
        return isResource;
    }

    public void setIsResource(boolean isResource) {
        isResource = isResource;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public QuestionMetadataDto getQuestionData() {
        return metadata;
    }

    public void setQuestionData(QuestionMetadataDto metadata) {
        this.metadata = metadata;
    }
}
