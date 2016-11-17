package com.quizzes.api.common.dto.controller.response;

import java.util.List;

public class QuestionDataDto {

    private String title;
    private String type;
    private List<AnswerDto> correctAnswer;
    private String body;
    private InteractionDto interaction;

    public QuestionDataDto(String title, String type, List<AnswerDto> correctAnswer,
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
