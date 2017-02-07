package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;

import java.util.UUID;

public interface CurrentContextProfileRepository {

    CurrentContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId);

    AssignedContextEntity findCurrentContextProfileByContextIdAndProfileId(UUID contextId, UUID profileId);

    CurrentContextProfile create(CurrentContextProfile currentContextProfile);

    void delete(CurrentContextProfile currentContextProfile);

}
