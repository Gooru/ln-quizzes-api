package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.GroupProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupProfileServiceImpl implements GroupProfileService {

    @Autowired
    GroupProfileRepository groupProfileRepository;

    @Override
    public void save(GroupProfile groupProfile) {
        groupProfileRepository.save(groupProfile);
    }
}

