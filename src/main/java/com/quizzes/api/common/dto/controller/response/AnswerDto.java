package com.quizzes.api.common.dto.controller.response;

public class AnswerDto {
    String value;

    public AnswerDto(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
