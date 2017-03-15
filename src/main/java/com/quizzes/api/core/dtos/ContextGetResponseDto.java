package com.quizzes.api.core.dtos;

import com.quizzes.api.core.dtos.controller.ContextDataDto;
import lombok.Data;

import java.util.UUID;

@Data
public class ContextGetResponseDto {

    private UUID contextId;
    private UUID collectionId;
    private Boolean isCollection;
    private UUID profileId;
    private UUID classId;
    private Boolean isActive;
    private Long startDate;
    private Long dueDate;
    private ContextDataDto contextData;
    private Long createdDate;
    private Long modifiedDate;
    private Boolean hasStarted;

}
