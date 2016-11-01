package com.quizzes.api.common.dto.controller;

public class AnswerDTO {
    String value;

    public AnswerDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
