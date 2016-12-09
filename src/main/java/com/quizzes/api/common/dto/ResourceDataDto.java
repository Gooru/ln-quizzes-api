package com.quizzes.api.common.dto;

import java.util.List;

public class ResourceDataDto {

    private String title;
    private String type;
    private List<AnswerDto> correctAnswer;
    private String body;
    private InteractionDto interaction;

    public ResourceDataDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AnswerDto> getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(List<AnswerDto> correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public InteractionDto getInteraction() {
        return interaction;
    }

    public void setInteraction(InteractionDto interaction) {
        this.interaction = interaction;
    }
}
