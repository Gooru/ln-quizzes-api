package com.quizzes.api.gooru.repository;

import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.repository.ContextRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface GooruContextRepository extends ContextRepository {

    @Query(value = "SELECT id, collection_id, context_body " +
            "FROM context " +
            "WHERE collection_id = :collectionId " +
            "AND context_body->>'courseId' = :courseId " +
            "AND context_body->>'classId' = :classId " +
            "AND context_body->>'unitId' = :unitId " +
            "AND context_body->>'lessonId' = :lessonId " +
            "LIMIT 1", nativeQuery = true)
    Context findByCollectionIdAndContext(@Param("collectionId") UUID collectionId,
                                         @Param("courseId") String courseId,
                                         @Param("classId") String classId,
                                         @Param("unitId") String unitId,
                                         @Param("lessonId") String lessonId);

}

