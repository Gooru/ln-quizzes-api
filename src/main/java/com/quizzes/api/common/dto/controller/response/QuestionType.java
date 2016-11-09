package com.quizzes.api.common.dto.controller.response;

public enum QuestionType {
    TrueFalse ("true_false"),
    SingleChoice ("single_choice"),
    MultipleChoice ("multiple_choice"),
    MultipleImage ("multiple_image"),
    Match ("match");

    private final java.lang.String literal;

    private QuestionType(java.lang.String literal) {
        this.literal = literal;
    }

}
