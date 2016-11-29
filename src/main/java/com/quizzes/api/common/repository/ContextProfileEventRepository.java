package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;

import java.util.List;
import java.util.UUID;

public interface ContextProfileEventRepository {

    List<ContextProfileEvent> findByContextProfileId(UUID contextProfileId);

    ContextProfileEvent save(ContextProfileEvent contextProfileEvent);

    ContextProfileEvent findByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId);
}
