package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.repository.GroupProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupProfileService {

    @Autowired
    GroupProfileRepository groupProfileRepository;

    public void save(GroupProfile groupProfile) {
        groupProfileRepository.save(groupProfile);
    }
}

