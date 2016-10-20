package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import org.springframework.stereotype.Service;

@Service
public interface GroupService {

    Group createGroup(Profile teacher);

}
