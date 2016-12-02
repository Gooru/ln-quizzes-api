package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.jooq.tables.pojos.Group;

import java.util.UUID;

public interface GroupRepository {

    Group save(Group group);

    Group findById(UUID id);

}
