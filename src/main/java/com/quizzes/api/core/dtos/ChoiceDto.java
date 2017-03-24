package com.quizzes.api.core.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ChoiceDto implements Serializable {

    private String text;
    private Boolean isFixed;
    private String value;
    private int sequence;

}
