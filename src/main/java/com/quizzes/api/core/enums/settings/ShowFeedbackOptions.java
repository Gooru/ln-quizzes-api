package com.quizzes.api.core.enums.settings;

import lombok.Getter;

import java.util.Arrays;

/**
 * Created by jcamacho on 2/27/17.
 */
public enum ShowFeedbackOptions {

    Immediate("immediate"),
    Summary("summary"),
    Never("never");

    @Getter
    private final String literal;

    ShowFeedbackOptions(String literal) {
        this.literal = literal;
    }

    public static ShowFeedbackOptions fromValue(String value) {
        return Arrays.stream(ShowFeedbackOptions.values())
                .filter(e -> e.literal.equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", value)));
    }
}
