package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.jooq.tables.pojos.Session;

import java.util.UUID;

public interface SessionRepository {

    Session save(Session session);

    Session findLastSessionByProfileId(UUID profileId);

    Session updateLastAccess(Session session);
}

