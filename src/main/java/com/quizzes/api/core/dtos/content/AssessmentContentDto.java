package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssessmentContentDto extends CollectionContentDto {

    @SerializedName("question")
    private List<ResourceContentDto> questions;

    @SerializedName("owner_id")
    private String ownerId;

    public AssessmentContentDto() {
    }

    public List<ResourceContentDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ResourceContentDto> questions) {
        this.questions = questions;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
