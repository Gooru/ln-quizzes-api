package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AnswerObjectEventContent {

    private String text;
    private String status;
    private int order;
    private UUID answerId;
    private long timeStamp;
    private boolean skip;

}
