package com.quizzes.api.content.gooru.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionDto {

    private String id;

    private String title;

    @SerializedName("content_subformat")
    private String contentSubformat;

    @SerializedName("sequence_id")
    private short sequence;

    @SerializedName("answer")
    private List<AnswerDto> answers;


    public QuestionDto() {
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

    public short getSequence() {
        return sequence;
    }

    public void setSequence(short sequence) {
        this.sequence = sequence;
    }

    public List<AnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDto> answers) {
        this.answers = answers;
    }

}
