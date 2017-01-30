package com.quizzes.api.core.dtos;

import java.util.List;

public class QuestionMetadataDto extends ResourceMetadataDto {

    private List<AnswerDto> correctAnswer;

    public QuestionMetadataDto() {
    }

    public List<AnswerDto> getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(List<AnswerDto> correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

}
