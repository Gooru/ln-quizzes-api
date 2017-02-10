package com.quizzes.api.util;

import com.quizzes.api.core.exceptions.InvalidOwnerException;

import java.util.UUID;

public class QuizzesUtils {

    private static final String ANONYMOUS_PROFILE = "anonymous";
    private static final UUID ANONYMOUS_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

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

    public static boolean isAnonymous(String profileId) {
        return profileId.equals(ANONYMOUS_PROFILE);
    }

    public static UUID getAnonymousId(){
        return ANONYMOUS_ID;
    }
}
