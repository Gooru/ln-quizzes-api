package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.repository.GroupProfileRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.common.model.tables.GroupProfile.GROUP_PROFILE;

@Repository
public class GroupProfileRepositoryImpl implements GroupProfileRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public void save(GroupProfile groupProfile) {
        jooq.insertInto(GROUP_PROFILE)
                .set(GROUP_PROFILE.ID, UUID.randomUUID())
                .set(GROUP_PROFILE.GROUP_ID, groupProfile.getGroupId())
                .set(GROUP_PROFILE.PROFILE_ID, groupProfile.getProfileId())
                .execute();
    }

}
