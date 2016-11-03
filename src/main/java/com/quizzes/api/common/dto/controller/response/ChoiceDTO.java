package com.quizzes.api.common.dto.controller.response;

public class ChoiceDTO {
    String  text;
    boolean isFixed;
    String  value;

    public ChoiceDTO(String text, boolean isFixed, String value) {
        this.text = text;
        this.isFixed = isFixed;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public String getValue() {
        return value;
    }
}
