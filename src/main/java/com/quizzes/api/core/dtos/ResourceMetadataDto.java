package com.quizzes.api.core.dtos;

import java.io.Serializable;
import java.util.List;

public class ResourceMetadataDto implements Serializable {

    private String title;
    private String type;
    private String url;
    private String body;
    private List<AnswerDto> correctAnswer;
    private InteractionDto interaction;

    public ResourceMetadataDto() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<AnswerDto> getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(List<AnswerDto> correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

}
