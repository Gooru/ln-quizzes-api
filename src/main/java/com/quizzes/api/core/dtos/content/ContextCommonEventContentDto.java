package com.quizzes.api.core.dtos.content;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ContextCommonEventContentDto {
    /**
     * @contentGooruId means collectionId for collection.play/stop and current resource id
     * for collection.resource.play/stop
     */
    private UUID contentGooruId;

    /**
     * Possible values are: start or stop
     */
    private String type;
    private String collectionType;
    private String collectionSubType;
    private UUID courseGooruId;
    private UUID classGooruId;
    private UUID unitGooruId;
    private UUID lessonGooruId;
    private String clientSource;
    private String source;
    private UUID appId;
    private UUID partnerId;
    private UUID tenantId;
}
