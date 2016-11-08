package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;

import java.util.List;
import java.util.UUID;

public interface ContextProfileEventRepository {

    List<ContextProfileEvent> findAttemptsByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId);
}
