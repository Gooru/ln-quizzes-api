package com.quizzes.api.common.dto.controller.response;

import com.quizzes.api.common.enums.QuestionTypeEnum;

import java.util.List;

public class QuestionDataDTO {
    String title;
    QuestionTypeEnum type;
    List<AnswerDTO> correctAnswer;
    String body;
    InteractionDTO interaction;

    public QuestionDataDTO(String title, QuestionTypeEnum type, List<AnswerDTO> correctAnswer, String body, InteractionDTO interaction) {
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

    public List<AnswerDTO> getCorrectAnswer() {
        return correctAnswer;
    }

    public String getBody() {
        return body;
    }

    public InteractionDTO getInteraction() {
        return interaction;
    }
}
