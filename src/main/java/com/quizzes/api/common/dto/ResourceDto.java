package com.quizzes.api.common.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;
import java.util.UUID;

public class ResourceDto {

    private UUID id;

    private boolean isResource;

    private int sequence;

    private QuestionDataDto questionData;

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

    public QuestionDataDto getQuestionData() {
        return questionData;
    }

    public void setQuestionData(QuestionDataDto questionData) {
        this.questionData = questionData;
    }
}
