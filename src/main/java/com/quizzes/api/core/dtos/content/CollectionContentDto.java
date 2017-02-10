package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class CollectionContentDto {

    private String id;

    private String title;

    @SerializedName("owner_id")
    private UUID ownerId;

    private Boolean isCollection;

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

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getCollection() {
        return isCollection;
    }

    public void setCollection(Boolean collection) {
        isCollection = collection;
    }

    public List<ResourceContentDto> getContent() {
        return content;
    }

    public void setContent(List<ResourceContentDto> content) {
        this.content = content;
    }
}
