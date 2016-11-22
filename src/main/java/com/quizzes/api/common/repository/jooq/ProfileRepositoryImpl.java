package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.model.tables.records.ProfileRecord;
import com.quizzes.api.common.repository.ProfileRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.quizzes.api.common.model.tables.Context.CONTEXT;
import static com.quizzes.api.common.model.tables.Group.GROUP;
import static com.quizzes.api.common.model.tables.Profile.PROFILE;
import static com.quizzes.api.common.model.tables.GroupProfile.GROUP_PROFILE;

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
    public List<Profile> save(final List<Profile> profiles) {
        List<Profile> result = new ArrayList<>();
        profiles.forEach(profile -> { Profile newProfile = insertProfile(profile);
                                        result.add(newProfile);});
        return result;
    }

    @Override
    public Profile findById(UUID id) {
        return new Profile(UUID.randomUUID(), "2423424", Lms.quizzes, "{\n" +
                "      \"id\": \"18b4a2f4-f0df-489d-93a8-11e104d6768b\",\n" +
                "      \"firstName\": \"Roger\",\n" +
                "      \"lastName\": \"Stevens\",\n" +
                "      \"username\": \"rogersteve\"\n" +
                "    }", null);

//        return jooq.select()
//                .from(PROFILE)
//                .where(PROFILE.ID.eq(id))
//                .fetchOneInto(Profile.class);
    }

    @Override
    public UUID findIdByExternalIdAndLmsId(String externalId, Lms lmsId) {
        return jooq.select(PROFILE.ID)
                .from(PROFILE)
                .where(PROFILE.EXTERNAL_ID.eq(externalId))
                .and(PROFILE.LMS_ID.eq(lmsId))
                .fetchOneInto(UUID.class);
    }

    @Override
    public Profile findByExternalIdAndLmsId(String externalId, Lms lmsId) {
        return jooq.select()
                .from(PROFILE)
                .where(PROFILE.EXTERNAL_ID.eq(externalId))
                .and(PROFILE.LMS_ID.eq(lmsId))
                .fetchOneInto(Profile.class);
    }

    @Override
    public List<UUID> findAssignedIdsByContextId(UUID contextId){
        return jooq.select(GROUP_PROFILE.PROFILE_ID)
                .from(CONTEXT)
                .join(GROUP).on(GROUP.ID.eq(CONTEXT.GROUP_ID))
                .join(GROUP_PROFILE).on(GROUP_PROFILE.GROUP_ID.eq(GROUP.ID))
                .where(CONTEXT.ID.eq(contextId))
                .fetchInto(UUID.class);
    }

    @Override
    public List<UUID> findexternalProfileIds(List<UUID> externalProfileIds, Lms lms){
        return jooq.select(PROFILE.EXTERNAL_ID)
                .from(PROFILE)
                .where(PROFILE.EXTERNAL_ID.in(externalProfileIds))
                .and(PROFILE.LMS_ID.eq(lms))
                .fetchInto(UUID.class);
    }

    @Override
    public List<UUID> findProfileIdsByExternalIdAndLms(List<UUID> externalProfileIds, Lms lms){
        return jooq.select(PROFILE.ID)
                .from(PROFILE)
                .where(PROFILE.EXTERNAL_ID.in(externalProfileIds))
                .and(PROFILE.LMS_ID.eq(lms))
                .fetchInto(UUID.class);
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
        return new Profile(UUID.randomUUID(), "2423424", Lms.quizzes, "{\n" +
                "      \"id\": \"18b4a2f4-f0df-489d-93a8-11e104d6768b\",\n" +
                "      \"firstName\": \"Roger\",\n" +
                "      \"lastName\": \"Stevens\",\n" +
                "      \"username\": \"rogersteve\"\n" +
                "    }", null);

//        return jooq.update(PROFILE)
//                .set(PROFILE.PROFILE_DATA, profile.getProfileData())
//                .where(PROFILE.ID.eq(profile.getId()))
//                .returning()
//                .fetchOne()
//                .into(Profile.class);
    }

}
