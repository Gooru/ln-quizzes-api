package com.quizzes.api.common.dto.controller.response;

public enum QuestionType {
    TrueFalse ("TrueFalse"),
    SingleChoice ("SingleChoice"),
    MultipleChoice ("MultipleChoice"),
    MultipleImage ("MultipleImage"),
    Match ("Match");

    private final java.lang.String literal;

    private QuestionType(java.lang.String literal) {
        this.literal = literal;
    }
}
