package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

    public Group createGroup(UUID ownerProfileId) {
        Group group = new Group(null, ownerProfileId, null, null);
        return groupRepository.save(group);
    }

}
