package com.quizzes.api.util;

import com.quizzes.api.core.exceptions.InvalidOwnerException;

public class QuizzesUtils {

    private static final String ANONYMOUS_PROFILE = "anonymous";

    public static void rejectAnonymous(String profileId) {
        if (profileId.equals(ANONYMOUS_PROFILE)){
            throw new InvalidOwnerException("Anonymous not allowed to run this service");
        }
    }

    public static void rejectAnonymous(String profileId, String message) {
        if (profileId.equals(ANONYMOUS_PROFILE)){
            throw new InvalidOwnerException(message);
        }
    }
}
