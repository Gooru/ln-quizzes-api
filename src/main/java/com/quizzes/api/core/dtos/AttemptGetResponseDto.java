package com.quizzes.api.core.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class AttemptGetResponseDto extends StartContextEventResponseDto {

    private UUID attemptId;
    private UUID profileId;
    private Long createdDate;
    private Long updatedDate;
    private EventSummaryDataDto eventSummary;
    private List<TaxonomySummaryDto> taxonomySummary;

}
