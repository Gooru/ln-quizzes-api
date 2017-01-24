package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.entities.SessionProfileEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.model.jooq.tables.pojos.Session;
import com.quizzes.api.common.repository.SessionRepository;
import com.quizzes.api.common.service.ConfigurationService;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.common.model.jooq.tables.Profile.PROFILE;
import static com.quizzes.api.common.model.jooq.tables.Session.SESSION;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    private final static double MINUTES_IN_HOUR = 60.0;
    private final static double HOURS_IN_DAY = 24.0;

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    private DSLContext jooq;

    @Override
    public Session save(Session session) {
        return jooq.insertInto(SESSION)
                .set(SESSION.ID, UUID.randomUUID())
                .set(SESSION.PROFILE_ID, session.getProfileId())
                .returning()
                .fetchOne()
                .into(Session.class);
    }

    @Override
    public Session findLastSessionByProfileId(UUID profileId) {
        double sessionInDays = configurationService.getSessionMinutes() / MINUTES_IN_HOUR / HOURS_IN_DAY;
        return jooq.select()
                .from(SESSION)
                .where(SESSION.PROFILE_ID.eq(profileId))
                .and(SESSION.LAST_ACCESS_AT.greaterOrEqual(DSL.currentTimestamp().sub(sessionInDays)))
                .orderBy(SESSION.LAST_ACCESS_AT.desc())
                .limit(1)
                .fetchOneInto(Session.class);
    }

    @Override
    public SessionProfileEntity findLastSessionProfileByClientIdAndExternalId(UUID clientId, String externalId) {
        double sessionInDays = configurationService.getSessionMinutes() / MINUTES_IN_HOUR / HOURS_IN_DAY;

        return jooq.select(PROFILE.ID.as("ProfileId"), PROFILE.CLIENT_ID, SESSION.ID.as("SessionId"))
                .from(PROFILE)
                .leftJoin(SESSION).on(SESSION.PROFILE_ID.eq(PROFILE.ID)
                            .and(SESSION.LAST_ACCESS_AT.greaterOrEqual(DSL.currentTimestamp().sub(sessionInDays))))
                .where(PROFILE.EXTERNAL_ID.eq(externalId))
                .and(PROFILE.CLIENT_ID.eq(clientId))
                .orderBy(SESSION.LAST_ACCESS_AT.desc())
                .limit(1)
                .fetchOneInto(SessionProfileEntity.class);
    }

    @Override
    public Session updateLastAccess(UUID sessionId) {
        return jooq.update(SESSION)
                .set(SESSION.LAST_ACCESS_AT, DSL.currentTimestamp())
                .where(SESSION.ID.eq(sessionId))
                .returning()
                .fetchOne()
                .into(Session.class);
    }

    @Override
    public SessionProfileEntity findSessionProfileEntityBySessionId(UUID sessionId) {
        return jooq.select(PROFILE.ID.as("ProfileId"), PROFILE.CLIENT_ID, SESSION.ID.as("SessionId"),
                SESSION.LAST_ACCESS_AT, DSL.currentTimestamp().as("CurrentTimestamp"))
                .from(SESSION)
                .join(PROFILE).on(PROFILE.ID.eq(SESSION.PROFILE_ID))
                .where(SESSION.ID.eq(sessionId))
                .fetchOneInto(SessionProfileEntity.class);
    }

    @Override
    public Profile findProfileBySessionId(UUID sessionId) {
        return jooq.select(PROFILE.ID, PROFILE.CLIENT_ID)
                .from(SESSION)
                .join(PROFILE).on(PROFILE.ID.eq(SESSION.PROFILE_ID))
                .where(SESSION.ID.eq(sessionId))
                .fetchOneInto(Profile.class);
    }

}