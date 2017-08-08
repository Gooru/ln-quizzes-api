package com.quizzes.api.core.dtos.analytics;

import com.quizzes.api.core.enums.AnswerStatus;
import com.quizzes.api.core.enums.GradingType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PayloadObjectResource {

    private AnswerStatus attemptStatus;

    private GradingType gradingType;

    /**
     * MC/TF/FIB/MA/etc.
     */
    private String questionType;

    /**
     * Low level mapped taxonomy ids on resource/question
     */
    private Map<String, String> taxonomyIds;

    private List<AnswerObject> answerObject;

    private boolean isStudent;

}
