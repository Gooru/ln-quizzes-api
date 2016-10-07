package com.quizzes.api.common.service;

import com.quizzes.api.common.model.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    Profile findById(UUID id){
        return profileRepository.findById(id);
    };
}
