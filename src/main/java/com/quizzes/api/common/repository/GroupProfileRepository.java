package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.jooq.tables.pojos.GroupProfile;

import java.util.List;
import java.util.UUID;

public interface GroupProfileRepository {

    void save(GroupProfile groupProfile);

    void delete(UUID groupId);

    List<GroupProfile> findGroupProfilesByGroupId(UUID id);
}