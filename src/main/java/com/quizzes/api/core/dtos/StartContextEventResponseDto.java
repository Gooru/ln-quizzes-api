package com.quizzes.api.core.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class StartContextEventResponseDto {

    private UUID contextId;
    private UUID collectionId;
    private UUID currentResourceId;
    private List<PostResponseResourceDto> events;

}