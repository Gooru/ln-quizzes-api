package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class ContextReactionEventContentDto extends ContextCommonEventContentDto {

    /**
     * Collection UUID of which this resource is a part of (currently playing)
     */
    private UUID parentGooruId;

    /**
     * This event should be collection play eventId.
     */
    private UUID parentEventId;

    /**
     * This event should be collection play eventId.
     * TODO: confirm possible values
     */
    private String reactionType;

    @Builder
    public ContextReactionEventContentDto(UUID contentGooruId, String type, String collectionType, String collectionSubType, UUID courseGooruId, UUID classGooruId, UUID unitGooruId, UUID lessonGooruId, String clientSource, String source, UUID appId, UUID partnerId, UUID tenantId, UUID parentGooruId, UUID parentEventId, String reactionType) {
        super(contentGooruId, type, collectionType, collectionSubType, courseGooruId, classGooruId, unitGooruId, lessonGooruId, clientSource, source, appId, partnerId, tenantId);
        this.parentGooruId = parentGooruId;
        this.parentEventId = parentEventId;
        this.reactionType = reactionType;
    }
}
