package com.quizzes.api.content.gooru.dto;

import com.google.gson.annotations.SerializedName;

public class AnswerDto {

    @SerializedName("answer_text")
    private String answerText;

    @SerializedName("answer_type")
    private String answerType;

    @SerializedName("is_correct")
    private boolean isCorrect;

    private int sequence;

    public AnswerDto() {
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

}
