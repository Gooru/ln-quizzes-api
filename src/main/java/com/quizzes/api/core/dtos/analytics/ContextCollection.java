package com.quizzes.api.core.dtos.analytics;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class ContextCollection extends ContextCommon {

    private int questionCount;
    private String additionalContext;

    @Builder
    public ContextCollection(UUID contentGooruId, String type, String collectionType, String collectionSubType,
                             UUID courseGooruId, UUID classGooruId, UUID unitGooruId, UUID lessonGooruId,
                             String clientSource, String source, UUID appId, UUID partnerId, UUID tenantId,
                             int questionCount, Long pathId, String pathType, String contentSource, 
                             String additionalContext) {
        super(contentGooruId, type, collectionType, collectionSubType, courseGooruId, classGooruId, unitGooruId,
                lessonGooruId, clientSource, source, appId, partnerId, tenantId, pathId, pathType, contentSource);
        this.questionCount = questionCount;
        this.additionalContext = additionalContext;
    }

}
