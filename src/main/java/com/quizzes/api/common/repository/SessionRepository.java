package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.entities.SessionProfileEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.model.jooq.tables.pojos.Session;

import java.util.UUID;

public interface SessionRepository {

    Session save(Session session);

    Session findLastSessionByProfileId(UUID profileId);

    Session updateLastAccess(UUID sessionId);

    SessionProfileEntity findSessionProfileEntityBySessionId(UUID sessionId);

    SessionProfileEntity findLastSessionProfileByClientIdAndExternalId(UUID clientId, String externalId);

    Profile findProfileBySessionId(UUID sessionId);
}

