package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.ContextProfile;

import java.util.List;
import java.util.UUID;

public interface ContextProfileRepository {

    ContextProfile findByContextIdAndProfileId(UUID externalId, UUID profileId);

    List<UUID> findContextProfileIdsByContextId(UUID contextId);

    void delete(UUID id);

    ContextProfile save(ContextProfile contextProfile);

}

