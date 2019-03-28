package com.quizzes.api.util;

import java.util.regex.Pattern;

/**
 * @author ashish.
 */

public final class NbspTrimmer {

    private static final Pattern NBSP_TRIM_PATTERN = Pattern.compile("(^\\h*)|(\\h*$)");

    private NbspTrimmer() {
        throw new AssertionError();
    }

    public static String trim(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return NBSP_TRIM_PATTERN.matcher(input).replaceAll("");
    }
}
