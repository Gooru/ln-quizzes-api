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
    ContextProfileEventRepository contextProfileEventRepository;

    List<ContextProfileEvent> findByContextProfileId(UUID contextProfileId) {
        return contextProfileEventRepository.findByContextProfileId(contextProfileId);
    }

    ContextProfileEvent save(ContextProfileEvent contextProfileEvent) {
        return contextProfileEventRepository.save(contextProfileEvent);
    }

    ContextProfileEvent findByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId) {
        return contextProfileEventRepository.findByContextProfileIdAndResourceId(contextProfileId, resourceId);
    }

    Map<UUID, List<AssigneeEventEntity>> findByContextId(UUID contextId){
        return contextProfileEventRepository.findByContextIdGroupByProfileId(contextId);
    }

    List<AssigneeEventEntity> findByContextIdAndProfileId(UUID contextId, UUID assigneeProfileId){
        return contextProfileEventRepository.findByContextIdAndProfileId(contextId, assigneeProfileId);
    }

    void deleteByContextProfileId(UUID contextProfileId) {
        contextProfileEventRepository.deleteByContextProfileId(contextProfileId);
    }

}
