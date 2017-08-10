package com.quizzes.api.core.dtos;

import com.quizzes.api.core.enums.PlayerEventSource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class EventContextDto {

    private PlayerEventSource eventSource;
    private String sourceUrl;
    private Long pathId;
    private String timezone;
    private String gradingType;
    private UUID partnerId;
    private UUID tenantId;
    private UUID classId;
    private UUID courseId;
    private UUID unitId;
    private UUID lessonId;
    private UUID collectionId;

    public Boolean isAttempt() {
        return eventSource != null && eventSource.equals(PlayerEventSource.CourseMap);
    }

}
