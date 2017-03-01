package com.quizzes.api.util;

import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.repositories.UtilsRepository;
import com.quizzes.api.core.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Component
public class QuizzesUtils {

    private static final String ANONYMOUS_PROFILE = "anonymous";
    private static final UUID ANONYMOUS_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Autowired
    private UtilsRepository utilsRepository;

    @Autowired
    private ConfigurationService configurationService;

    public static void rejectAnonymous(String profileId) {
        rejectAnonymous(profileId, "Anonymous not allowed to run this service");
    }

    public static void rejectAnonymous(String profileId, String message) {
        if (profileId.equals(ANONYMOUS_PROFILE)) {
            throw new InvalidOwnerException(message);
        }
    }

    public static boolean isAnonymous(String profileId) {
        return profileId.equals(ANONYMOUS_PROFILE);
    }

    public static UUID getAnonymousId() {
        return ANONYMOUS_ID;
    }

    public static UUID resolveProfileId(String profileId) {
        if (isAnonymous(profileId)) {
            return QuizzesUtils.getAnonymousId();
        } else {
            return UUID.fromString(profileId);
        }
    }

    public long getCurrentTimestamp() {
        return utilsRepository.getCurrentTimestamp();
    }
}
