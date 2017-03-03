package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PayloadObjectResourceEventContentDto {

    /**
     * Status like: correct/incorrect/skipped
     */
    private String attemptStatus;

    /**
     * MC/TF/FIB/MA/etc.
     */
    private String questionType;

    /**
     * Low level mapped taxonomy ids on resource/question
     */
    private List<UUID> taxonomyIds;

    private List<AnswerObjectEventContent> answerObject;

}
