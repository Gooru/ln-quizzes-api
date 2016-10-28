package com.quizzes.api.common.service;

import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ProfileService {

    Profile findById(UUID id);

    Profile findByExternalIdAndLmsId(String externalId, Lms lms);

    Profile save(Profile profile);

}
