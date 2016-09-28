package com.quizzes.realtime.repository;

import com.quizzes.realtime.model.EventIndex;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


import java.util.List;

@RepositoryRestResource(collectionResourceRel = "eventIndex", path = "eventIndexes")
public interface EventIndexRepository extends CrudRepository<EventIndex, String> {

    EventIndex findFirstByCollectionUniqueIdAndUserId(String collectionUniqueId, String userId);

    List<EventIndex> findByCollectionUniqueIdOrderByUserIdAsc(String collectionUniqueId);

}
