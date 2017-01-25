package com.quizzes.api.core.model.entities;

import java.sql.Timestamp;
import java.util.UUID;

public interface SessionProfileEntity {

    UUID getSessionId();

    void setSessionId(UUID sessionId);

    UUID getProfileId();

    void setProfileId(UUID profileId);

    UUID getClientId();

    void setClientId(UUID clientId);

    Timestamp getLastAccessAt();

    void setLastAccessAt(Timestamp lastAccessAt);

    Timestamp getCurrentTimestamp();

    void setCurrentTimestamp(Timestamp currentTimestamp);

}
