package com.quizzes.api.common.service;

import com.quizzes.api.common.model.jooq.tables.pojos.Group;
import com.quizzes.api.common.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

    public Group createGroup(UUID ownerProfileId) {
        Group group = new Group();
        group.setOwnerProfileId(ownerProfileId);
        return groupRepository.save(group);
    }

    public Group findById(UUID id){
        return groupRepository.findById(id);
    }

}
