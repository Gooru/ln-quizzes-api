package com.quizzes.api.common.model;

public enum Lms {

    gooru("gooru"),
    quizzes("quizzes"),
    itsLearning("its-learning");

    private String value;

    Lms(String name) {
        this.value = name;
    }

    public String getValue() {
        return value;
    }

}
