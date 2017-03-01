package com.quizzes.api.core.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class TaxonomySummaryDto extends EventSummaryDataDto {

    private String      taxonomyId = "";
    private List<UUID>  resources = null;

}
