package com.quizzes.api.core.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class TaxonomySummaryDto implements Serializable {

    private String      taxonomyId = "";
    private long        totalTimeSpent = 0;
    private short       averageReaction = 0;
    private short       averageScore = 0;
    private short       totalCorrect = 0;
    private short       totalAnswered = 0;
    private List<UUID>  resources = null;

}
