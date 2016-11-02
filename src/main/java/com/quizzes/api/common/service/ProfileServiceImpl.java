package com.quizzes.api.common.service;

import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Override
    public Profile findById(UUID id) {
        return null;
    }

    public Profile findByExternalIdAndLmsId(String externalId, Lms lms) {
        return profileRepository.findByExternalIdAndLmsId(externalId, lms);
    }

    @Override
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }
}