package com.quizzes.api.core.services.content;

import com.quizzes.api.core.model.jooq.tables.pojos.Collection;
import com.quizzes.api.core.model.jooq.tables.pojos.Profile;

public interface CollectionContentService {

    /**
     * Creates a new {@link Collection} in Quizzes based on the content's provider Assessment
     * if the assessment belongs to a different owner then the assessment is copied
     * and the new {@link Collection} is based on that copy
     * @param externalCollectionId content's provider Assessment ID
     * @param owner the Quizzes {@link Profile} of the {@link Collection} owner
     * @return the Quizzes new {@link Collection}
     */
    Collection createCollection(String externalCollectionId, Profile owner);

}
