package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.repository.ContextProfileRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ContextProfileRepositoryImpl implements ContextProfileRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public ContextProfile findByContextIdAndProfileId(UUID externalId, UUID profileId) {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(UUID.randomUUID());
        contextProfile.setContextId(UUID.randomUUID());
        contextProfile.setProfileId(UUID.randomUUID());
        contextProfile.setIsComplete(false);
        contextProfile.setCurrentResourceId(UUID.randomUUID());
        return contextProfile;
    }

    @Override
    public ContextProfile save(ContextProfile contextProfile) {
        contextProfile.setId(UUID.randomUUID());
        contextProfile.setContextId(UUID.randomUUID());
        contextProfile.setProfileId(UUID.randomUUID());
        contextProfile.setIsComplete(false);
        contextProfile.setCurrentResourceId(UUID.randomUUID());
        return contextProfile;
    }

    @Override
    public List<UUID> findContextProfileIdsByContextId(UUID contextId){
        List<UUID> ids = new ArrayList<>();
        ids.add(UUID.randomUUID());
        ids.add(UUID.randomUUID());
        return ids;
    }

    @Override
    public void delete(UUID id){
        //TODO: Implement functionality
    }

}
