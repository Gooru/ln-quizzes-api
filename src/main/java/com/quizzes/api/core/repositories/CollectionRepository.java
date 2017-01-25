package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.jooq.tables.pojos.Collection;

import java.util.UUID;

public interface CollectionRepository {

    Collection save(Collection collection);

    Collection findById(UUID id);

    Collection findByExternalId(String externalId);

    Collection findByOwnerProfileIdAndExternalParentId(UUID ownerProfileId,  String externalParentId);
}