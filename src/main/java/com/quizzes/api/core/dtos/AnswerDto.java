package com.quizzes.api.core.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
public class AnswerDto implements Serializable {

    private String value;

    @ApiModelProperty(hidden = true)
    private UUID id;

    public AnswerDto(String value) {
        this.value = value;
    }
}
