package com.quizzes.api.content.gooru.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssessmentDto extends CollectionDto {

    @SerializedName("question")
    private List<QuestionDto> questions;


    public AssessmentDto() {
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

}
