package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ContextEventContentDto {
    @SerializedName("contentGooruId")
    private UUID collectionId;
    private String type;
    private int questionCount;
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
