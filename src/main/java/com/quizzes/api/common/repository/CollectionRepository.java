package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.Collection;
import com.quizzes.api.common.model.Context;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "collection", path = "collection")
public interface CollectionRepository extends JpaRepository<Collection, UUID> {

    Collection findByExternalId(String id);

    Collection save(Collection collection);
}