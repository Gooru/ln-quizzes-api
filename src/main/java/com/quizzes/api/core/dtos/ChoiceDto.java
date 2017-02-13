package com.quizzes.api.core.dtos;

import java.io.Serializable;

public class ChoiceDto implements Serializable {

    private String text;
    private boolean isFixed;
    private String value;
    private int sequence;

    public ChoiceDto() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getIsFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

}
