package com.quizzes.api.common.service;

import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.common.repository.CurrentContextProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentContextProfileService {

    @Autowired
    CurrentContextProfileRepository currentContextProfileRepository;

    public CurrentContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId) {
        CurrentContextProfile currentContextProfile =
                currentContextProfileRepository.findByContextIdAndProfileId(contextId, profileId);
        if (currentContextProfile == null) {
            throw new ContentNotFoundException("Current Context Profile not found for Context ID: " + contextId
                    + " and Profile ID: " + profileId);
        }
        return currentContextProfile;
    }

    public CurrentContextProfile create(CurrentContextProfile currentContextProfile) {
        return currentContextProfileRepository.create(currentContextProfile);
    }

    public CurrentContextProfile delete(CurrentContextProfile currentContextProfile) {
        return currentContextProfileRepository.create(currentContextProfile);
    }

}

