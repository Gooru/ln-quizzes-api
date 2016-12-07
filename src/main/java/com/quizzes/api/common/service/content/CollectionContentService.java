package com.quizzes.api.common.service.content;

import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.model.jooq.tables.pojos.Collection;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;

public interface CollectionContentService {

    Collection createCollection(String externalCollectionId, Profile owner);

}
