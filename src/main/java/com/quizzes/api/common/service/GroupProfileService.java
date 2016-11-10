package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.repository.GroupProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupProfileService {

    @Autowired
    GroupProfileRepository groupProfileRepository;

    public void save(GroupProfile groupProfile) {
        groupProfileRepository.save(groupProfile);
    }

    public List<GroupProfile> findGroupProfilesByGroupId(UUID id){
        return groupProfileRepository.findGroupProfilesByGroupId(id);
    }

}

