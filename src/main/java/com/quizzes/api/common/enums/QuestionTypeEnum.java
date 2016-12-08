package com.quizzes.api.common.enums;

import com.google.gson.annotations.SerializedName;

public enum QuestionTypeEnum {

    @SerializedName("true_false")
    TrueFalse ("true_false"),

    @SerializedName("single_choice")
    SingleChoice ("single_choice"),

    @SerializedName("multiple_choice")
    MultipleChoice ("multiple_choice"),

    @SerializedName("multiple_image")
    MultipleImage ("multiple_image"),

    @SerializedName("match")
    Match ("match"),

    @SerializedName("none")
    None ("none");

    private final String literal;

    private QuestionTypeEnum(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    public static QuestionTypeEnum fromString(String text) {
        if (text != null) {
            for (QuestionTypeEnum b : QuestionTypeEnum.values()) {
                if (text.equalsIgnoreCase(b.getLiteral())) {
                    return b;
                }
            }
        }
        return null;
    }

}
