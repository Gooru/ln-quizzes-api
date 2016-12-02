package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.jooq.tables.pojos.Group;
import com.quizzes.api.common.repository.GroupRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.common.model.jooq.tables.Group.GROUP;

@Repository
public class GroupRepositoryImpl implements GroupRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Group save(final Group group) {
        if (group.getId() == null) {
            return insertGroup(group);
        } else {
            return updateGroup(group);
        }
    }

    public Group findById(UUID id){
        //TODO: mocked group, replace with a valid implementation
        Group group = new Group();
        group.setId(id);
        group.setOwnerProfileId(UUID.randomUUID());
        return group;
    }

    private Group insertGroup(final Group group) {
        return jooq.insertInto(GROUP)
                .set(GROUP.ID, UUID.randomUUID())
                .set(GROUP.OWNER_PROFILE_ID, group.getOwnerProfileId())
                .set(GROUP.GROUP_DATA, group.getGroupData())
                .returning()
                .fetchOne()
                .into(Group.class);
    }

    private Group updateGroup(final Group group) {
        return jooq.update(GROUP)
                .set(GROUP.GROUP_DATA, group.getGroupData())
                .where(GROUP.ID.eq(group.getId()))
                .returning()
                .fetchOne()
                .into(Group.class);
    }

}

