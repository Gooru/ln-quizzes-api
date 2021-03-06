package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.entities.ContextProfileEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;

import java.util.UUID;

public interface CurrentContextProfileRepository {

    CurrentContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId);

    /**
     * Finds the currentContextProfile, collectionId, and the current contextProfile for an assignee in a context.
     *
     * @param contextId context ID
     * @param profileId profile assigned to the context
     * @return the currentProfileContext in the {@link ContextProfileEntity} found
     */
    ContextProfileEntity findCurrentContextProfileByContextIdAndProfileId(UUID contextId, UUID profileId);

    CurrentContextProfile create(CurrentContextProfile currentContextProfile);

    void delete(CurrentContextProfile currentContextProfile);

}
