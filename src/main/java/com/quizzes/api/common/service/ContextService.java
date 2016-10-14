package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.model.Context;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ContextService {

    ResponseEntity<Context> getContext(String externalCollectionId, AssignmentDTO assignmentDTO);

}
