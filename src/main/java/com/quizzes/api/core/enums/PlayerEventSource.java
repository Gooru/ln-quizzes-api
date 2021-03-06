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
    IndependentActivity("ILActivity"),

    @SerializedName("competencyMastery")
    CompetencyMastery("competencyMastery"),

    @SerializedName("diagnostic")
    Diagnostic("diagnostic");

    @Getter
    private final String literal;

}
