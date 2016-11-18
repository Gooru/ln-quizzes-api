package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    public Profile findById(UUID id) {
        return null;
    }

    public IdResponseDto findIdByExternalIdAndLmsId(String externalId, Lms lms) {
        UUID id = profileRepository.findIdByExternalIdAndLmsId(externalId, lms);
        IdResponseDto result = new IdResponseDto();
        result.setId(id);
        return result;
    }

    public Profile findByExternalIdAndLmsId(String externalId, Lms lms) {
        return profileRepository.findByExternalIdAndLmsId(externalId, lms);
    }

    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    public List<UUID> findAssignedIdsByContextId(UUID contextId){
        return profileRepository.findAssignedIdsByContextId(contextId);
    }
}
