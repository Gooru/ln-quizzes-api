package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ContextProfileEventRepository {

    List<ContextProfileEvent> findByContextProfileId(UUID contextProfileId);

    List<UUID> findReourceIdsByContextProfileId(UUID contextProfileId);

    ContextProfileEvent save(ContextProfileEvent contextProfileEvent);

    ContextProfileEvent findByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId);

    Map<UUID, List<AssigneeEventEntity>> findByContextIdGroupByProfileId(UUID contextId);

    void deleteByContextProfileId(UUID contextProfileId);
}
