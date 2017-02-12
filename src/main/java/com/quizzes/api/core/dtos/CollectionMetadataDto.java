package com.quizzes.api.core.dtos;

import java.io.Serializable;

public class CollectionMetadataDto implements Serializable {

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
