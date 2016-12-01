package com.quizzes.api.common.service;

import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.common.repository.ContextProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContextProfileService {

    @Autowired
    ContextProfileRepository contextProfileRepository;

    public ContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId) {
        return contextProfileRepository.findByContextIdAndProfileId(contextId, profileId);
    }

    public List<UUID> findContextProfileIdsByContextId(UUID contextId){
        return contextProfileRepository.findContextProfileIdsByContextId(contextId);
    }

    public void delete(UUID id){
        contextProfileRepository.delete(id);
    }

    public ContextProfile save(ContextProfile contextProfile) {
        return contextProfileRepository.save(contextProfile);
    }

}
