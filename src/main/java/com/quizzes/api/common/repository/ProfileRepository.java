package com.quizzes.api.common.repository;


import com.quizzes.api.common.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Profile findById(UUID id);
}
