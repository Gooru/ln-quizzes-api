package com.quizzes.api.core.services;

import com.quizzes.api.core.dtos.AttemptIdsResponseDto;
import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.repositories.ContextProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContextProfileService {

    @Autowired
    ContextProfileRepository contextProfileRepository;

    public ContextProfile findById(UUID contextProfileId) throws ContentNotFoundException {
        ContextProfile contextProfile = contextProfileRepository.findById(contextProfileId);
        if (contextProfile == null) {
            throw new ContentNotFoundException("Not Found ContextProfile Id: " + contextProfile);
        }
        return contextProfile;
    }

    public ContextProfile findByContextIdAndProfileId(UUID contextId, UUID profileId) throws ContentNotFoundException {
        ContextProfile contextProfile = contextProfileRepository.findByContextIdAndProfileId(contextId, profileId);
        if (contextProfile == null) {
            throw new ContentNotFoundException("Not Found ContextProfile for Context Id: " + contextId
                    + " and Profile Id: " + profileId);
        }
        return contextProfile;
    }

    public List<UUID> findContextProfileIdsByContextId(UUID contextId) {
        return contextProfileRepository.findContextProfileIdsByContextId(contextId);
    }

    public AttemptIdsResponseDto findContextProfileAttemptIds(UUID contextId, UUID assigneeId) {
        AttemptIdsResponseDto result = new AttemptIdsResponseDto();
        List<IdResponseDto> ids = contextProfileRepository.
                findContextProfileIdsByContextIdAndProfileId(contextId, assigneeId).
                stream().map(uuid -> {IdResponseDto idResponseDto = new IdResponseDto();
                idResponseDto.setId(uuid);
                return idResponseDto;
                }).collect(Collectors.toList());
        result.setAttempts(ids);
        return result;
    }

    public ContextProfile save(ContextProfile contextProfile) {
        return contextProfileRepository.save(contextProfile);
    }

}
