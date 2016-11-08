package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.tables.pojos.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface GroupProfileService {

    void save(GroupProfile groupProfile);

    List<GroupProfile> getGroupProfilesByGroupId(UUID id);
}
