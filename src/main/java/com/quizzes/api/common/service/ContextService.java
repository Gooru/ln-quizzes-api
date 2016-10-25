package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import org.springframework.stereotype.Service;

@Service
public interface ContextService {

    Context createContext(AssignmentDTO body, Lms lms); // We need to define the parameters and correct response value for the create context (assignment)

}
