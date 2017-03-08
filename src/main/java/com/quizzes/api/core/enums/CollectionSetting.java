package com.quizzes.api.core.enums;

import lombok.Getter;

public enum CollectionSetting {

    ShowFeedback("show_feedback"),
    AttemptsAllowed("attempts_allowed");

    @Getter
    private final String literal;

    CollectionSetting(String literal) {
        this.literal = literal;
    }
}
