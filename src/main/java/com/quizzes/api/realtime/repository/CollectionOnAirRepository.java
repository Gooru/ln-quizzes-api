package com.quizzes.api.realtime.repository;

import com.quizzes.api.realtime.model.CollectionOnAir;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;


@RepositoryRestResource(collectionResourceRel = "collectionOnAir", path = "collectionOnAir")
public interface CollectionOnAirRepository extends CrudRepository<CollectionOnAir, UUID> {

    CollectionOnAir findFirstByClassIdAndCollectionId(String classId, String collectionId);

    List<CollectionOnAir> findByClassId(String classId);

}
