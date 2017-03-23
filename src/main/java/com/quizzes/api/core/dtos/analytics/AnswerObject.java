package com.quizzes.api.core.dtos.analytics;

import com.quizzes.api.core.enums.AnswerStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerObject {

    private String text;
    private AnswerStatus status;
    private int order;
    private String answerId;
    private long timeStamp;
    private boolean skip;

}
