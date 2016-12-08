package com.quizzes.api.common.dto;

import java.util.List;

public class ResourceDataDto {

    private String title;
    private String type;
    private List<AnswerDto> correctAnswer;
    private String body;
    private InteractionDto interaction;

    public ResourceDataDto(String title, String type, List<AnswerDto> correctAnswer,
                           String body, InteractionDto interaction) {
        this.title = title;
        this.type = type;
        this.correctAnswer = correctAnswer;
        this.body = body;
        this.interaction = interaction;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public List<AnswerDto> getCorrectAnswer() {
        return correctAnswer;
    }

    public String getBody() {
        return body;
    }

    public InteractionDto getInteraction() {
        return interaction;
    }

}
