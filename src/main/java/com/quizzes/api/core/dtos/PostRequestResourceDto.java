package com.quizzes.api.core.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class PostRequestResourceDto extends CommonResourceDto {

    /* These property are only used to save that field in the database */
    @ApiModelProperty(hidden = true)
    private int score = 0;

    @ApiModelProperty(hidden = true)
    private Boolean isSkipped = true;

}
