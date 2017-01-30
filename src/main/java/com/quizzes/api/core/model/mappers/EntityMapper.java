package com.quizzes.api.core.model.mappers;

import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;

public class EntityMapper {

    public static Context mapContextEntityToContext(ContextEntity contextEntity) {
        Context context = new Context();
        context.setId(contextEntity.getId());
        context.setCollectionId(contextEntity.getCollectionId());
        context.setContextData(contextEntity.getContextData());
        context.setIsActive(contextEntity.getIsActive());
        return context;
    }

}
