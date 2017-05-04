package com.quizzes.api.core.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class OnResourceEventPostRequestDto {

    private EventContextDto eventContext;
    private PostRequestResourceDto previousResource;

}
