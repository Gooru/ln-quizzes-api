package com.quizzes.api.core.enums;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PlayerEventSource {

    @SerializedName("coursemap")
    CourseMap("coursemap"),

    @SerializedName("dailyclassactivity")
    DailyClass("dailyclassactivity"),

    @SerializedName("ILActivity")
    IndependentActivity("ILActivity");

    @Getter
    private final String literal;

    public static PlayerEventSource fromString(String literal) {
        if (literal != null) {
            return null;
        }
        for(PlayerEventSource value : values()) {
            if (value.getLiteral().equalsIgnoreCase(literal)) {
                return value;
            }
        }
        return null;
    }
}
