package com.quizzes.api.content.gooru.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.response.QuestionType;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.service.content.CollectionContentService;
import com.quizzes.api.content.gooru.dto.AssessmentDto;
import com.quizzes.api.content.gooru.rest.CollectionRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CollectionContentServiceImpl implements CollectionContentService {

    private static final String GOORU_MULTIPLE_CHOICE_QUESTION = "multiple_choice_question";

    //private static final Map<String, String> questionTypeMap = new HashMap<>()


    @Autowired
    CollectionRestClient collectionRestClient;

    @Override
    public Collection createCollectionCopy(String externalCollectionId, Profile owner) {

        AssessmentDto assessmentDto = collectionRestClient.getAssessment(externalCollectionId);

        Collection collection = new Collection();
        collection.setExternalId(assessmentDto.getId());
        collection.setLmsId(Lms.gooru);
        collection.setIsCollection(false);
        collection.setOwnerProfileId(owner.getId());


        Map<String, Object> collectionDataMap = new HashMap<>();
        collectionDataMap.put("title", assessmentDto.getTitle());
        //collectionDataMap.put("", assessmentDto.get)
        collection.setCollectionData(new Gson().toJson(collectionDataMap));
        return collection;
    }

    /*
    private String mapContentType(String orignalContentFormat) {
        switch (orignalContentFormat) {
            case GOORU_MULTIPLE_CHOICE_QUESTION: {
                return QuestionType.SingleChoice
            }
        }
    }
    */

}
