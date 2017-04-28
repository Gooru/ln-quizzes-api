package com.quizzes.api.core.dtos.analytics;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class ContextReaction extends ContextCommon {

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
    private ContextReaction(UUID contentGooruId, String type, String collectionType,
                            String collectionSubType, UUID courseGooruId, UUID classGooruId,
                            UUID unitGooruId, UUID lessonGooruId, String clientSource, String source,
                            UUID appId, UUID partnerId, UUID tenantId, UUID parentGooruId,
                            UUID parentEventId, String reactionType, String contentSource) {
        super(contentGooruId, type, collectionType, collectionSubType, courseGooruId, classGooruId, unitGooruId,
                lessonGooruId, clientSource, source, appId, partnerId, tenantId, contentSource);
        this.parentGooruId = parentGooruId;
        this.parentEventId = parentEventId;
        this.reactionType = reactionType;
    }
}
