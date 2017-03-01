package com.quizzes.api.core.dtos.content;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
public class SessionEventDto {
    private UUID apiKey;
    private UUID sessionId;
    private String sessionToken;
}
