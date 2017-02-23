package com.quizzes.api.core.dtos.content;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CollectionContentDto {

    private String id;

    private String title;

    @SerializedName("owner_id")
    private UUID ownerId;

    private Boolean isCollection;

    private List<ResourceContentDto> content;

    private Map<String, Object> setting;

    private Map<String, Object> taxonomy;

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

    public Boolean getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(Boolean collection) {
        isCollection = collection;
    }

    public List<ResourceContentDto> getContent() {
        return content;
    }

    public void setContent(List<ResourceContentDto> content) {
        this.content = content;
    }

    public Map<String, Object> getSetting() {
        return setting;
    }

    public void setSetting(Map<String, Object> setting) {
        this.setting = setting;
    }

    public Map<String, Object> getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(Map<String, Object> taxonomy) {
        this.taxonomy = taxonomy;
    }
}
