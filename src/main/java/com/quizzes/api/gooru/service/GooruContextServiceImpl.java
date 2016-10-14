package com.quizzes.api.gooru.service;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.gooru.repository.GooruContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("gooru-lms")
public class GooruContextServiceImpl implements ContextService {

    private final static String CLASS_ID = "classId";
    private final static String LESSON_ID = "lessonId";
    private final static String UNIT_ID = "unitId";
    private final static String COURSE_ID = "courseId";

    @Autowired
    CollectionService collectionService;

    @Autowired
    GooruContextRepository gooruContextRepository;

    @Override
    public ResponseEntity<Context> getContext(String externalCollectionId, AssignmentDTO assignmentDTO) {
//        Collection collection = collectionService.getOrCreateCollection(externalCollectionId);
//        Context context = gooruContextRepository.findByCollectionIdAndContext(collection.getId(),
//                assignmentDTO.getContext().get(COURSE_ID),
//                assignmentDTO.getContext().get(CLASS_ID),
//                assignmentDTO.getContext().get(UNIT_ID),
//                assignmentDTO.getContext().get(LESSON_ID));
//        if (context != null) {
//            return new ResponseEntity<>(context, HttpStatus.OK);
//        }
//        context = new Context(collection);
//        context.setContextBody(new JSONObject(assignmentDTO.getContext()).toJSONString());
//
        return new ResponseEntity<>(new Context(), HttpStatus.CREATED);
    }

}

