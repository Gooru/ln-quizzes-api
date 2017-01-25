package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.jooq.tables.pojos.GroupProfile;

import java.util.List;
import java.util.UUID;

public interface GroupProfileRepository {

    void save(GroupProfile groupProfile);

    void delete(UUID groupId);

    List<GroupProfile> findGroupProfilesByGroupId(UUID id);
}