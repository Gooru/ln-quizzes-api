package com.quizzes.api.core.enums;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AnswerStatus {

    @SerializedName("skipped")
    Skipped("skipped"),
    @SerializedName("correct")
    Correct("correct"),
    @SerializedName("incorrect")
    Incorrect("incorrect");

    @Getter
    private final String literal;

    @Override
    public String toString() {
        return getLiteral();
    }
}
