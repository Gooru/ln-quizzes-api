package com.quizzes.api.core.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class EventContextDto {

    private String eventSource;
    private String sourceUrl;
    private Long pathId;
    private String timezone;
    private String gradingType;
    private UUID partnerId;
    private UUID tenantId;

}
