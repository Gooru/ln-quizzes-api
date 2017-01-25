package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;

import java.util.List;
import java.util.UUID;

public interface ContextProfileRepository {

    ContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId);

    List<UUID> findContextProfileIdsByContextId(UUID contextId);

    void delete(UUID id);

    ContextProfile save(ContextProfile contextProfile);

    ContextProfile findById(UUID contextProfileId);
}

