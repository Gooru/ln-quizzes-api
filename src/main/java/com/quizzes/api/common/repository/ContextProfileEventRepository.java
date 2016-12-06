package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.entities.ContextEventEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfileEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ContextProfileEventRepository {

    List<ContextProfileEvent> findByContextProfileId(UUID contextProfileId);

    ContextProfileEvent save(ContextProfileEvent contextProfileEvent);

    ContextProfileEvent findByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId);

    Map<UUID, List<ContextEventEntity>> findAllStudentEventsByContextId(UUID contextId);
}
