package com.quizzes.api.core.dtos;

public class MetadataDto {

    private String title;
    private String description;

    public MetadataDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
