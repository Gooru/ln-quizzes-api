package com.quizzes.api.common.dto.controller.response;

public class ChoiceDto {
    String  text;
    boolean isFixed;
    String  value;

    public ChoiceDto(String text, boolean isFixed, String value) {
        this.text = text;
        this.isFixed = isFixed;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public boolean getIsFixed() {
        return isFixed;
    }

    public String getValue() {
        return value;
    }
}
