package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.repository.ContextProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContextProfileService {

    @Autowired
    ContextProfileRepository contextProfileRepository;

    public ContextProfile findContextProfileByContextIdAndProfileId(UUID contextId, UUID profileId) {
        return contextProfileRepository.findByContextIdAndProfileId(contextId, profileId);
    }

    public ContextProfile save(ContextProfile contextProfile) {
        return contextProfileRepository.save(contextProfile);
    }

}
