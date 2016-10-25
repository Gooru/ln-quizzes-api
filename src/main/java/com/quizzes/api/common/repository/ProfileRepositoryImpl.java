package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.model.tables.records.ProfileRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static com.quizzes.api.common.model.tables.Profile.PROFILE;

public class ProfileRepositoryImpl implements ProfileRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Profile save(Profile profile) {
        if (profile.getId() == null) {
            profile.setId(UUID.randomUUID());
            ProfileRecord profileRecord = jooq.newRecord(PROFILE, profile);
            profileRecord.changed(PROFILE.CREATED_AT, false); // Add this the use the current date
            jooq.executeInsert(profileRecord);
        } else {
            ProfileRecord profileRecord = jooq.newRecord(PROFILE, profile);
            jooq.update(PROFILE).set(profileRecord).where(PROFILE.ID.eq(profileRecord.getId())).execute();
        }
        return profile;
    }

    @Override
    public Profile findById(UUID id) {
        return jooq.select()
                .from(PROFILE)
                .where(PROFILE.ID.eq(id))
                .fetchAny().into(Profile.class);
    }

    @Override
    public Profile findByExternalIdAndLms(UUID externalId, Lms lmsId) {
        return jooq.select()
                .from(PROFILE)
                .where(PROFILE.EXTERNAL_ID.eq(String.valueOf(externalId)))
                .and(PROFILE.LMS_ID.eq(lmsId))
                .fetchOneInto(Profile.class);
    }

}
