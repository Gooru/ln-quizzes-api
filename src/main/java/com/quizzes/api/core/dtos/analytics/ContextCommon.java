package com.quizzes.api.core.dtos.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ContextCommon {

    /**
     * @contentGooruId means collectionId for collection.play/stop
     * It means currentResourceId for collection.resource.play/stop and reaction.create
     */
    private UUID contentGooruId;

    /**
     * @type only works for collection.play/stop and collection.resource.play/stop
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
    private Long pathId;
    private String pathType;
    private String contentSource;

}
