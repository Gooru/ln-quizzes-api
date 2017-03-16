package com.quizzes.api.core.services;

import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.entities.ContextProfileEventEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.repositories.ContextProfileEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContextProfileEventService {

    @Autowired
    private ContextProfileEventRepository contextProfileEventRepository;

    public List<ContextProfileEvent> findByContextProfileId(UUID contextProfileId) {
        return contextProfileEventRepository.findByContextProfileId(contextProfileId);
    }

    public ContextProfileEvent findByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId) {
        return contextProfileEventRepository.findByContextProfileIdAndResourceId(contextProfileId, resourceId);
    }

    public Map<UUID, List<AssigneeEventEntity>> findByContextId(UUID contextId) {
        return contextProfileEventRepository.findByContextIdGroupByProfileId(contextId);
    }

    public List<ContextProfileEventEntity> findByContextProfileIdAndProfileId(UUID contextProfileId, UUID profileId) {
        return contextProfileEventRepository.findByContextProfileIdAndProfileId(contextProfileId,profileId);
    }

    public ContextProfileEvent save(ContextProfileEvent contextProfileEvent) {
        return contextProfileEventRepository.save(contextProfileEvent);
    }

    public void deleteByContextProfileId(UUID contextProfileId) {
        contextProfileEventRepository.deleteByContextProfileId(contextProfileId);
    }

}
