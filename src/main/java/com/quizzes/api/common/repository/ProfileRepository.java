package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;

import java.util.List;
import java.util.UUID;

public interface ProfileRepository {

    Profile findById(UUID id);

    Profile findByExternalIdAndLmsId(String externalId, Lms lmsId);

    Profile save(Profile id);

    List<Profile> findAssigneesDataByContextId(UUID contextId);
}
