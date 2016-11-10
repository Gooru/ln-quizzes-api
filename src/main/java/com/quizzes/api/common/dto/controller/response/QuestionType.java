package com.quizzes.api.common.dto.controller.response;

import com.google.gson.annotations.SerializedName;

public enum QuestionType {

    @SerializedName("true_false")
    TrueFalse ("true_false"),

    @SerializedName("single_choice")
    SingleChoice ("single_choice"),

    @SerializedName("multiple_choice")
    MultipleChoice ("multiple_choice"),

    @SerializedName("multiple_image")
    MultipleImage ("multiple_image"),

    @SerializedName("match")
    Match ("match");

    private final java.lang.String literal;

    private QuestionType(java.lang.String literal) {
        this.literal = literal;
    }

}
