package com.quizzes.api.core.enums;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GradingType {

    @SerializedName("teacher")
    Teacher("teacher"),

    @SerializedName("system")
    System("system"),

    @SerializedName("self")
    Self("self");

    @Getter
    private final String literal;

    @Override
    public String toString() {
        return getLiteral();
    }

    public static GradingType fromString(String literal) {
        for(GradingType value : values()) {
            if (value.getLiteral().equalsIgnoreCase(literal)) {
                return value;
            }
        }
        return Self;
    }
}
