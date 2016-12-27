package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.model.jooq.tables.pojos.Session;
import com.quizzes.api.common.repository.SessionRepository;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.common.model.jooq.tables.Profile.PROFILE;
import static com.quizzes.api.common.model.jooq.tables.Session.SESSION;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    private final static double MINUTES_IN_HOUR = 60.0;
    private final static double HOURS_IN_DAY = 24.0;

    @Value("${session.time.minutes}")
    private double sessionMinutes;

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
        double sessionInDays = sessionMinutes / MINUTES_IN_HOUR / HOURS_IN_DAY;
        return jooq.select()
                .from(SESSION)
                .where(SESSION.PROFILE_ID.eq(profileId))
                .and(SESSION.LAST_ACCESS_AT.greaterOrEqual(DSL.currentTimestamp().sub(sessionInDays)))
                .orderBy(SESSION.LAST_ACCESS_AT.desc())
                .limit(1)
                .fetchOneInto(Session.class);
    }

    @Override
    public Session updateLastAccess(Session session) {
        return jooq.update(SESSION)
                .set(SESSION.LAST_ACCESS_AT, DSL.currentTimestamp())
                .where(SESSION.ID.eq(session.getId()))
                .returning()
                .fetchOne()
                .into(Session.class);
    }

    @Override
    public Profile findProfileBySessionId(UUID sessionId) {
        return jooq.select()
                .from(SESSION)
                .join(PROFILE).on(PROFILE.ID.eq(SESSION.PROFILE_ID))
                .where(SESSION.ID.eq(sessionId))
                .fetchOneInto(Profile.class);
    }

}