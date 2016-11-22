package com.quizzes.api.common.dto.controller.response;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;
import java.util.UUID;

public class CollectionDataResourceDto {

    private UUID id;

    private boolean isResource;

    private int sequence;

    @ApiModelProperty(hidden = true)
    @SerializedName("questionData")
    private Map<String, Object> questions;

    private transient QuestionDataDto questionData;

    public CollectionDataResourceDto() {
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

    public Map<String, Object> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<String, Object> questions) {
        this.questions = questions;
    }

    public QuestionDataDto getQuestionData() {
        return questionData;
    }

    public void setQuestionData(QuestionDataDto questionData) {
        this.questionData = questionData;
    }
}
