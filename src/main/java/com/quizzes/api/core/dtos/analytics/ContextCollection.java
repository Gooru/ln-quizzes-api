package com.quizzes.api.core.dtos.analytics;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class ContextCollection extends ContextCommon {

    private int questionCount;

    @Builder
    public ContextCollection(UUID contentGooruId, String type, String collectionType, String collectionSubType, UUID courseGooruId, UUID classGooruId, UUID unitGooruId, UUID lessonGooruId, String clientSource, String source, UUID appId, UUID partnerId, UUID tenantId, int questionCount) {
        super(contentGooruId, type, collectionType, collectionSubType, courseGooruId, classGooruId, unitGooruId, lessonGooruId, clientSource, source, appId, partnerId, tenantId);
        this.questionCount = questionCount;
    }
}
