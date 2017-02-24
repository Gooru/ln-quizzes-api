package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AssessmentContentDto extends CollectionContentDto implements Serializable {

    @SerializedName("question")
    private List<ResourceContentDto> questions;

    public AssessmentContentDto() {
    }

    public List<ResourceContentDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ResourceContentDto> questions) {
        this.questions = questions;
    }

}
