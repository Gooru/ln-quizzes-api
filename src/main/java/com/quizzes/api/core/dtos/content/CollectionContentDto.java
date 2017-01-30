package com.quizzes.api.core.dtos.content;

import java.util.List;

public class CollectionContentDto {

    private String id;

    private String title;

    private List<ResourceContentDto> content;

    public CollectionContentDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ResourceContentDto> getContent() {
        return content;
    }

    public void setContent(List<ResourceContentDto> content) {
        this.content = content;
    }
}
