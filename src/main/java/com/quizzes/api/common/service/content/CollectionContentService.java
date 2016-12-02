package com.quizzes.api.common.service.content;

import com.quizzes.api.common.model.jooq.tables.pojos.Collection;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;

public interface CollectionContentService {

    Collection createCollectionCopy(String externalCollectionId, Profile owner);

}
