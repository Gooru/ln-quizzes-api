package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.jooq.tables.pojos.CurrentContextProfile;

import java.util.UUID;

public interface CurrentContextProfileRepository {

    CurrentContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId);

    CurrentContextProfile save(CurrentContextProfile currentContextProfile);

    void finish(CurrentContextProfile currentContextProfile);
}
