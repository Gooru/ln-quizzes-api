package com.quizzes.api.core.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ResourceEventDto {

    private CommonResourceDto resourceEventData;
    private EventContextDto eventContext;

}
