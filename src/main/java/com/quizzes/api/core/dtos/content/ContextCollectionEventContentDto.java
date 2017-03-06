package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class ContextCollectionEventContentDto extends ContextCommonEventContentDto {

    private int questionCount;

    @Builder
    public ContextCollectionEventContentDto(UUID contentGooruId, String type, String collectionType, String collectionSubType, UUID courseGooruId, UUID classGooruId, UUID unitGooruId, UUID lessonGooruId, String clientSource, String source, UUID appId, UUID partnerId, UUID tenantId, int questionCount) {
        super(contentGooruId, type, collectionType, collectionSubType, courseGooruId, classGooruId, unitGooruId, lessonGooruId, clientSource, source, appId, partnerId, tenantId);
        this.questionCount = questionCount;
    }
}
