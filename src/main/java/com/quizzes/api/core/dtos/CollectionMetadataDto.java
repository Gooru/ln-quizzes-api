package com.quizzes.api.core.dtos;

public class CollectionMetadataDto {

    private String title;

    public CollectionMetadataDto(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
