package com.quizzes.api.core.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ContextProfileDataDto {
    private UUID resourceEventId;

}
