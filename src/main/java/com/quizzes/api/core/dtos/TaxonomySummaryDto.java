package com.quizzes.api.core.dtos;

import java.util.List;
import java.util.UUID;

public class TaxonomySummaryDto {

    private String      taxonomyId = "";
    private long        totalTimeSpent = 0;
    private short       averageReaction = 0;
    private short       averageScore = 0;
    private short       totalCorrect = 0;
    private short       totalAnswered = 0;
    private List<UUID>  resources = null;

    public String getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(String taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    public long getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public short getAverageReaction() {
        return averageReaction;
    }

    public void setAverageReaction(short averageReaction) {
        this.averageReaction = averageReaction;
    }

    public short getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(short averageScore) {
        this.averageScore = averageScore;
    }

    public short getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(short totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public short getTotalAnswered() {
        return totalAnswered;
    }

    public void setTotalAnswered(short totalAnswered) {
        this.totalAnswered = totalAnswered;
    }

    public List<UUID> getResources() {
        return resources;
    }

    public void setResources(List<UUID> resources) {
        this.resources = resources;
    }
}
