package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;

/**
 * This Dto will work for collection.resource.play/collection.stop
 */
@Data
@Builder
public class EventResourceContentDto extends EventContentCommonDto{

    private ContextCollectionEventContentDto context;
    private PayloadObjectResourceEventContentDto payLoadObject;

}
