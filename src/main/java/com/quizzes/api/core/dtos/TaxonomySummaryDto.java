package com.quizzes.api.core.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class TaxonomySummaryDto extends EventSummaryDataDto implements Serializable {

    private String      taxonomyId = "";
    private List<UUID>  resources = null;

}
