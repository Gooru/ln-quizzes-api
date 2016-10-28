package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.Group;

public interface GroupRepository {

    Group save(Group group);
}
