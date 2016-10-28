package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    @Override
    public Group createGroup(UUID ownerProfileId) {
        return null;
    }

}
