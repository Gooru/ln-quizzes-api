package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.jooq.tables.pojos.Collection;

import java.util.UUID;

public interface CollectionRepository {

    Collection save(Collection collection);

    Collection findById(UUID id);

    /**
     * Since an Assessment can be a copy of another Assessment or an original Assessment we should be able to find a collection
     * by any of external ID o external parent ID and also by the Quizzes owner {@link com.quizzes.api.common.model.jooq.tables.pojos.Profile}
     * @param externalId Content Provider ID of the Assessment or the parent Assessment
     * @param ownerProfileId Quizzes owner {@link com.quizzes.api.common.model.jooq.tables.pojos.Profile} ID
     * @return the {@link Collection} object
     */
    Collection findByExternalIdorExternalParentIdandOwner(String externalId, UUID ownerProfileId);

}