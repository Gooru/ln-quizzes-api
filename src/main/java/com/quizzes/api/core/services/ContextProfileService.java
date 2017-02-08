package com.quizzes.api.core.services;

import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.repositories.ContextProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContextProfileService {

    @Autowired
    ContextProfileRepository contextProfileRepository;

    public ContextProfile findById(UUID contextProfileId) throws ContentNotFoundException {
        ContextProfile contextProfile = contextProfileRepository.findById(contextProfileId);
        if (contextProfile == null) {
            throw new ContentNotFoundException("Not Found ContextProfile Id: " + contextProfile);
        }
        return contextProfile;
    }

    public ContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId) throws ContentNotFoundException {
        ContextProfile contextProfile = contextProfileRepository.findByContextIdAndProfileId(contextId, profileId);
        if (contextProfile == null) {
            throw new ContentNotFoundException("Not Found ContextProfile for Context Id: " + contextId
                    + " and Profile Id: " + profileId);
        }
        return contextProfile;
    }

    public List<UUID> findContextProfileIdsByContextId(UUID contextId) {
        return contextProfileRepository.findContextProfileIdsByContextId(contextId);
    }

    public List<UUID> findContextProfileIdsByContextIdAndProfileId(UUID contextId, UUID assigneeId) {
        return contextProfileRepository.findContextProfileIdsByContextIdAndProfileId(contextId, assigneeId);
    }

    public ContextProfile save(ContextProfile contextProfile) {
        return contextProfileRepository.save(contextProfile);
    }

}
