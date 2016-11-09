package com.quizzes.api.content.gooru.service;

import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.service.content.CollectionContentService;
import com.quizzes.api.content.gooru.dto.AssessmentDto;
import com.quizzes.api.content.gooru.rest.CollectionRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionContentServiceImpl implements CollectionContentService {

    @Autowired
    CollectionRestClient collectionRestClient;

    @Override
    public Collection createCollectionCopy(String externalCollectionId, Profile owner) {

        AssessmentDto assessment = collectionRestClient.getAssessment(externalCollectionId);

        // TODO this is a temporal implementation
        Collection collection = new Collection();
        collection.setOwnerProfileId(owner.getId());
        return collection;
    }

}
