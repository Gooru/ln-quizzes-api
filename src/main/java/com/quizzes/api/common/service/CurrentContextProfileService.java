package com.quizzes.api.common.service;

import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.common.repository.CurrentContextProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentContextProfileService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CurrentContextProfileRepository currentContextProfileRepository;

    public CurrentContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId) {
        CurrentContextProfile currentContext =
                currentContextProfileRepository.findByContextIdAndProfileId(contextId, profileId);
        if (currentContext == null) {
            throw new ContentNotFoundException("Not Found Context Id: " + contextId + " for profile ID: " + profileId);
        }
        return currentContext;
    }

    public CurrentContextProfile save(CurrentContextProfile currentContextProfile) {
        return currentContextProfileRepository.save(currentContextProfile);
    }

    public void finish(CurrentContextProfile currentContextProfile) {
        currentContextProfileRepository.finish(currentContextProfile);
    }

    public CurrentContextProfile startAttempt(CurrentContextProfile currentContextProfile) {
        return currentContextProfileRepository.startAttempt(currentContextProfile);
    }
}

