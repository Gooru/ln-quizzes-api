package com.quizzes.api.content.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssessmentDto extends CollectionDto {

    @SerializedName("question")
    private List<QuestionDto> questions;

    @SerializedName("owner_id")
    private String ownerId;

    public AssessmentDto() {
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
