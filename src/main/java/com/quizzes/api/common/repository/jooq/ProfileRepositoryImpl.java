package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.common.model.tables.Profile.PROFILE;

@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Profile save(final Profile profile) {
        if (profile.getId() == null) {
            return insertProfile(profile);
        } else {
            return updateProfile(profile);
        }
    }

    @Override
    public Profile findById(UUID id) {
        return jooq.select()
                .from(PROFILE)
                .where(PROFILE.ID.eq(id))
                .fetchOneInto(Profile.class);
    }

    @Override
    public Profile findByExternalIdAndLmsId(String externalId, Lms lmsId) {
        return jooq.select()
                .from(PROFILE)
                .where(PROFILE.EXTERNAL_ID.eq(externalId))
                .and(PROFILE.LMS_ID.eq(lmsId))
                .fetchOneInto(Profile.class);
    }

    private Profile insertProfile(final Profile profile) {
        return jooq.insertInto(PROFILE)
                .set(PROFILE.ID, UUID.randomUUID())
                .set(PROFILE.EXTERNAL_ID, profile.getExternalId())
                .set(PROFILE.LMS_ID, profile.getLmsId())
                .set(PROFILE.PROFILE_DATA, profile.getProfileData())
                .returning()
                .fetchOne()
                .into(Profile.class);
    }

    private Profile updateProfile(final Profile profile) {
        return jooq.update(PROFILE)
                .set(PROFILE.PROFILE_DATA, profile.getProfileData())
                .where(PROFILE.ID.eq(profile.getId()))
                .returning()
                .fetchOne()
                .into(Profile.class);
    }

}
