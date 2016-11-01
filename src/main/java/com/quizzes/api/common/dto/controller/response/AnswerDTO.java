package com.quizzes.api.common.dto.controller.response;

public class AnswerDTO {
    String value;

    public AnswerDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
