package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface GroupService {

    Group createGroup(UUID ownerProfileId);

    Group findById(UUID Id);
}
