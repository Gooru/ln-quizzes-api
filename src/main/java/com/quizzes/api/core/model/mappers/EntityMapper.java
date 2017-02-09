package com.quizzes.api.core.model.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quizzes.api.core.dtos.ContextGetResponseDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    @Autowired
    private Gson gson = new GsonBuilder().create();

    public Context mapContextEntityToContext(ContextEntity contextEntity) {
        Context context = new Context();
        context.setId(contextEntity.getContextId());
        context.setCollectionId(contextEntity.getCollectionId());
        context.setContextData(contextEntity.getContextData());
        context.setIsActive(contextEntity.getIsActive());
        return context;
    }

    public ContextGetResponseDto mapContextEntityToContextGetResponseDto(ContextEntity context) {
        ContextGetResponseDto createdContext = new ContextGetResponseDto();
        createdContext.setContextId(context.getContextId());
        createdContext.setCollectionId(context.getCollectionId());
        createdContext.setIsCollection(context.getIsCollection());
        createdContext.setClassId(context.getClassId());
        createdContext.setIsActive(context.getIsActive());
        createdContext.setStartDate(context.getStartDate() != null ? context.getStartDate().getTime() : 0);
        createdContext.setDueDate(context.getDueDate() != null ? context.getDueDate().getTime() : 0);
        createdContext.setContextData(gson.fromJson(context.getContextData(), ContextDataDto.class));
        createdContext.setCreatedDate(context.getCreatedAt().getTime());
        createdContext.setModifiedDate(context.getUpdatedAt().getTime());
        return createdContext;
    }

    public ContextGetResponseDto mapAssignedContextEntityToContextGetResponseDto(AssignedContextEntity context) {
        ContextGetResponseDto createdContext = mapContextEntityToContextGetResponseDto(context);
        createdContext.setProfileId(context.getProfileId());
        createdContext.setHasStarted(context.getCurrentContextProfileId() != null);
        createdContext.setIsActive(null);
        createdContext.setModifiedDate(null);
        return createdContext;
    }

}
