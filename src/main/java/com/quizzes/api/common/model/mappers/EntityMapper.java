package com.quizzes.api.common.model.mappers;

import com.quizzes.api.common.model.entities.ContextEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;

public class EntityMapper {

    public static Context mapContextEntityToContext(ContextEntity contextEntity) {
        Context context = new Context();
        context.setId(contextEntity.getId());
        context.setCollectionId(contextEntity.getCollectionId());
        context.setGroupId(contextEntity.getGroupId());
        context.setContextData(contextEntity.getContextData());
        context.setIsActive(contextEntity.getIsActive());
        return context;
    }

}
