package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

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
    private Map<String, String> taxonomyIds;

    private List<AnswerObjectEventContent> answerObject;

}
