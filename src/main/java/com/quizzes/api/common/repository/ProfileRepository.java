package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;

import java.util.List;
import java.util.UUID;

public interface ProfileRepository {

    Profile findById(UUID id);

    UUID findIdByExternalIdAndLmsId(String externalId, Lms lmsId);

    Profile findByExternalIdAndLmsId(String externalId, Lms lmsId);

    Profile save(Profile id);

    Profile findAssigneeInContext(UUID contextId, UUID profileId);

    List<UUID> findAssignedIdsByContextId(UUID contextId);

    List<String> findExternalProfileIds(List<String> externalProfileIds, Lms lms);

    List<UUID> findProfileIdsByExternalIdAndLms(List<String> externalProfileIds, Lms lms);

    List<Profile> findProfilesByExternalIdAndLms(List<String> externalProfileIds, Lms lms);
}
