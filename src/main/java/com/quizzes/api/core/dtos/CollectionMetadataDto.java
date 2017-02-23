package com.quizzes.api.core.dtos;

import java.io.Serializable;
import java.util.Map;

public class CollectionMetadataDto implements Serializable {

    private String title;
    private Map<String, Object> setting;
    private Map<String, Object> taxonomy;

    public CollectionMetadataDto() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
