package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.ContextDTO;
import com.quizzes.api.common.model.Context;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ContextService {

    ResponseEntity<Context> getContext(String externalCollectionId, ContextDTO contextDTO);

}
