package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ContextResourceEventContentDto extends ContextCollectionEventContentDto {

    /**
     * Collection UUID of which this resource is a part of (currently playing)
     */
    private UUID parentGooruId;

    /**
     * This event should be collection play eventId.
     */
    private UUID parentEventId;

    /**
     * Resource current ItemId
     */
    private UUID itemId;

    /**
     * It’s hold type of resource sample values : resource or question
     */
    private String resourceType;

}
