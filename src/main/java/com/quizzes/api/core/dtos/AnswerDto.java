package com.quizzes.api.core.dtos;

import java.io.Serializable;

public class AnswerDto implements Serializable {

    private String value;

    public AnswerDto() {
    }

    public AnswerDto(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
