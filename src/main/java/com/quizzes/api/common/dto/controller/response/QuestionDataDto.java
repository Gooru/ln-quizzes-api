package com.quizzes.api.common.dto.controller.response;

import com.quizzes.api.common.enums.QuestionTypeEnum;

import java.util.List;

public class QuestionDataDto {
    String title;
    QuestionTypeEnum type;
    List<AnswerDto> correctAnswer;
    String body;
    InteractionDto interaction;

    public QuestionDataDto(String title, QuestionTypeEnum type, List<AnswerDto> correctAnswer, String body, InteractionDto interaction) {
        this.title = title;
        this.type = type;
        this.correctAnswer = correctAnswer;
        this.body = body;
        this.interaction = interaction;
    }

    public String getTitle() {
        return title;
    }

    public QuestionTypeEnum getType() {
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
