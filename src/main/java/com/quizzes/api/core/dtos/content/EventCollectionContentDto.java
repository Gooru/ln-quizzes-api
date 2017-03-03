package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;

/**
 * This Dto will work for collection.play/collection.stop
 */
@Data
@Builder
public class EventCollectionContentDto extends EventContentCommonDto{

    private ContextCollectionEventContentDto context;
    private PayloadObjectCollectionEventContentDto payLoadObject;

}
