package com.quizzes.api.core.services;

import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.entities.ContextProfileEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.repositories.CurrentContextProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentContextProfileService {

    @Autowired
    private CurrentContextProfileRepository currentContextProfileRepository;

    public CurrentContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId) {
        CurrentContextProfile currentContextProfile =
                currentContextProfileRepository.findByContextIdAndProfileId(contextId, profileId);
        if (currentContextProfile == null) {
            throw new ContentNotFoundException("Current Context Profile not found for Context ID: " + contextId
                    + " and Profile ID: " + profileId);
        }
        return currentContextProfile;
    }

    /**
     * Finds the currentContextProfile, collectionId, and the current contextProfile for an assignee in a context.
     * If contextProfileId does not exist it is because the profile is not assigned in the context
     *
     * @param contextId context ID
     * @param profileId profile assigned to the context
     * @return the currentProfileContext in the {@link ContextProfileEntity} found
     */
    public ContextProfileEntity findCurrentContextProfileByContextIdAndProfileId(UUID contextId, UUID profileId) {
        ContextProfileEntity currentContextProfile =
                currentContextProfileRepository.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId);
        if (currentContextProfile == null || currentContextProfile.getContextProfileId() == null) {
            throw new ContentNotFoundException("Current Context Profile not found for Context ID: " + contextId
                    + " and Profile ID: " + profileId);
        }
        return currentContextProfile;
    }

    public CurrentContextProfile create(CurrentContextProfile currentContextProfile) {
        return currentContextProfileRepository.create(currentContextProfile);
    }

    public void delete(CurrentContextProfile currentContextProfile) {
        currentContextProfileRepository.delete(currentContextProfile);
    }

}

