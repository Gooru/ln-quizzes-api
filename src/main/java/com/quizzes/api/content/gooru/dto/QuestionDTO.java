package com.quizzes.api.content.gooru.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionDTO {

    private String id;

    private String title;

    @SerializedName("content_subformat")
    private String contentSubformat;

    @SerializedName("sequence_id")
    private int sequence;

    @SerializedName("answer")
    private List<AnswerDTO> answers;


    public QuestionDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentSubformat() {
        return contentSubformat;
    }

    public void setContentSubformat(String contentSubformat) {
        this.contentSubformat = contentSubformat;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }

}
