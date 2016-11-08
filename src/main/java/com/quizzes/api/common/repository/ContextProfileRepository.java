package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.ContextProfile;

import java.util.UUID;

public interface ContextProfileRepository {

    ContextProfile findByContextIdAndProfileId(UUID externalId, UUID profileId);

    ContextProfile save(ContextProfile contextProfile);

}

