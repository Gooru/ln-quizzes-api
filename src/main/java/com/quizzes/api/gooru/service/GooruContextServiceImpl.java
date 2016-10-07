package com.quizzes.api.gooru.service;

import com.quizzes.api.common.dto.controller.ContextDTO;
import com.quizzes.api.common.model.Collection;
import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.ContextServiceImpl;
import com.quizzes.api.gooru.repository.GooruContextRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("Gooru")
@Primary
class GooruContextServiceImpl extends ContextServiceImpl {

    private final static String CLASS_ID = "classId";
    private final static String LESSON_ID = "lessonId";
    private final static String UNIT_ID = "unitId";
    private final static String COURSE_ID = "courseId";

    @Autowired
    CollectionService collectionService;

    @Autowired
    GooruContextRepository gooruContextRepository;

    @Override
    public ResponseEntity<Context> getContext(String externalCollectionId, ContextDTO contextDTO) {
        Collection collection = collectionService.getOrCreateCollection(externalCollectionId);
        Context context = gooruContextRepository.findByCollectionIdAndContext(collection.getId(),
                contextDTO.getContext().get(COURSE_ID),
                contextDTO.getContext().get(CLASS_ID),
                contextDTO.getContext().get(UNIT_ID),
                contextDTO.getContext().get(LESSON_ID));
        if (context != null) {
            return new ResponseEntity<>(context, HttpStatus.OK);
        }
        context = new Context(collection);
        context.setContextBody(new JSONObject(contextDTO.getContext()).toJSONString());

        return new ResponseEntity<>(gooruContextRepository.save(context), HttpStatus.CREATED);
    }

}

