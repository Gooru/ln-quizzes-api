package com.quizzes.api.core.dtos.analytics;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Session {
    private UUID apiKey;
    private UUID sessionId;
    private String sessionToken;
}
