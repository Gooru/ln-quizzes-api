package com.quizzes.realtime.repository;

import com.quizzes.realtime.model.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "event", path = "events")
public interface EventRepository extends CrudRepository<Event, UUID> {


}
