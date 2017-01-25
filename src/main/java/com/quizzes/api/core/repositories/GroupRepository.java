package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.jooq.tables.pojos.Group;

import java.util.UUID;

public interface GroupRepository {

    Group save(Group group);

    Group findById(UUID id);

}
