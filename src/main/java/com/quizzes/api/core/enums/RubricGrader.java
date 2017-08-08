package com.quizzes.api.core.enums;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RubricGrader {

    @SerializedName("self")
    Self("self"),
    @SerializedName("teacher")
    Teacher("teacher");

    @Getter
    private final String literal;

    @Override
    public String toString() {
        return getLiteral();
    }

    public static RubricGrader fromString(String literal) {
        for(RubricGrader value : values()) {
            if (value.getLiteral().equalsIgnoreCase(literal)) {
                return value;
            }
        }
        return Self;
    }
}
