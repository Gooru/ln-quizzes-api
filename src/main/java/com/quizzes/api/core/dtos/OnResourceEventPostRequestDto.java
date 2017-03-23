package com.quizzes.api.core.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OnResourceEventPostRequestDto {

    private PostRequestResourceDto previousResource;

}
