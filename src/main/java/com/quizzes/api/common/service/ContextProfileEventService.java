package com.quizzes.api.common.service;

import com.quizzes.api.common.model.entities.ContextEventEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.repository.ContextProfileEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContextProfileEventService {

    @Autowired
    ContextProfileEventRepository contextProfileEventRepository;

    List<ContextProfileEvent> findByContextProfileId(UUID contextProfileId){
        return contextProfileEventRepository.findByContextProfileId(contextProfileId);
    }

    ContextProfileEvent save(ContextProfileEvent contextProfileEvent){
        return contextProfileEventRepository.save(contextProfileEvent);
    }

    ContextProfileEvent findByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId) {
        return contextProfileEventRepository.findByContextProfileIdAndResourceId(contextProfileId, resourceId);
    }

    Map<UUID, List<ContextEventEntity>> findAllStudentEventsByContextId(UUID contextId){
        return contextProfileEventRepository.findAllStudentEventsByContextId(contextId);
    }

}
