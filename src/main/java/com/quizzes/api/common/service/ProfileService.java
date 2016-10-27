package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ProfileService {

    Profile findById(UUID id);

    Profile findOrCreateAssignee(ProfileDTO profileDTO, Lms lms);

    Profile findOrCreateOwner(ProfileDTO profileDTO, Lms lms);

}
