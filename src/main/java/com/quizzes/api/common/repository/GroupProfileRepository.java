package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.GroupProfile;

import java.util.List;
import java.util.UUID;

public interface GroupProfileRepository {

    void save(GroupProfile groupProfile);

    List<GroupProfile> findGroupProfilesByGroupId(UUID id);
}