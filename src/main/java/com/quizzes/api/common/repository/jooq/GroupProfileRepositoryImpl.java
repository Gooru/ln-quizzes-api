package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.jooq.tables.pojos.GroupProfile;
import com.quizzes.api.common.repository.GroupProfileRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.quizzes.api.common.model.jooq.tables.GroupProfile.GROUP_PROFILE;

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

    @Override
    public void delete(UUID groupId) {
        jooq.deleteFrom(GROUP_PROFILE)
            .where(GROUP_PROFILE.GROUP_ID.eq(groupId))
            .execute();
    }

    public List<GroupProfile> findGroupProfilesByGroupId(UUID id){
        List<GroupProfile> groupProfiles = new ArrayList<>();

        GroupProfile asignee1 = new GroupProfile();
        asignee1.setGroupId(id);
        asignee1.setId(UUID.randomUUID());
        asignee1.setProfileId(UUID.randomUUID());
        groupProfiles.add(asignee1);

        GroupProfile asignee2 = new GroupProfile();
        asignee2.setGroupId(id);
        asignee2.setId(UUID.randomUUID());
        asignee2.setProfileId(UUID.randomUUID());
        groupProfiles.add(asignee2);

        return groupProfiles;
    }

}
