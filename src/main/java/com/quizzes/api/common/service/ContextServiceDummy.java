package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class ContextServiceDummy {

    public Context createContext(AssignmentDTO body, Lms lms) {
        Context context = generateMockContext();

        return context;
    }

    public Context update(UUID context, ContextPutRequestDto contextPutRequestDto) {
        return null;
    }

    public static Context generateMockContext() {
        Context result = new Context();
        result.setId(UUID.randomUUID());
        result.setCollectionId(UUID.randomUUID());
        result.setGroupId(UUID.randomUUID());
        result.setContextData(new String());
        result.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return result;
    }

}
