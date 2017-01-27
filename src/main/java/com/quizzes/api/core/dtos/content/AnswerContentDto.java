package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;

public class AnswerContentDto {

    @SerializedName("answer_text")
    private String answerText;

    @SerializedName("answer_type")
    private String answerType;

    @SerializedName("is_correct")
    private String isCorrect;

    private String id;

    private int sequence;

    public AnswerContentDto() {
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

    public String isCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(String isCorrect) {
        this.isCorrect = isCorrect;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
