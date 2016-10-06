package com.quizzes.api.common.repository;


import com.quizzes.api.common.model.Context;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface ContextRepository extends JpaRepository<Context, UUID> {

    Collection<Context> findById(UUID id);
}
