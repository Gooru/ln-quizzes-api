package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssessmentContentDto extends CollectionContentDto {

    @SerializedName("question")
    private List<QuestionContentDto> questions;

    @SerializedName("owner_id")
    private String ownerId;

    public AssessmentContentDto() {
    }

    public List<QuestionContentDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionContentDto> questions) {
        this.questions = questions;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
