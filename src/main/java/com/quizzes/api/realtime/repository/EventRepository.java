package com.quizzes.api.realtime.repository;

import com.quizzes.api.realtime.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "event", path = "events")
public interface EventRepository extends JpaRepository<Event, UUID> {


}
