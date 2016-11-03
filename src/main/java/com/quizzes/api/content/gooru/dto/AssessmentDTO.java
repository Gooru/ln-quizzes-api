package com.quizzes.api.content.gooru.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssessmentDTO extends CollectionDTO {

    @SerializedName("question")
    private List<QuestionDTO> questions;

    public AssessmentDTO() {
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }

}
