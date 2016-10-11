package com.quizzes.api.common.service;

import com.quizzes.api.common.model.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ProfileService {

    Profile findById(UUID id);

}
