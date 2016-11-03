package com.quizzes.api.common.dto.controller.response;

import java.util.List;

public class QuestionDataDTO {
    String title;
    QuestionType type;
    List<AnswerDTO> correctAnswer;
    String body;
    InteractionDTO interaction;

    public QuestionDataDTO(String title, QuestionType type, List<AnswerDTO> correctAnswer, String body, InteractionDTO interaction) {
        this.title = title;
        this.type = type;
        this.correctAnswer = correctAnswer;
        this.body = body;
        this.interaction = interaction;
    }

    public String getTitle() {
        return title;
    }

    public QuestionType getType() {
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
