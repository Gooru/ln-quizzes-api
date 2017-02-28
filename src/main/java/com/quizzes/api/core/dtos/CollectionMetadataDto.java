package com.quizzes.api.core.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class CollectionMetadataDto implements Serializable {

    private String title;
    private Map<String, Object> setting;
    private Map<String, Object> taxonomy;

    public Object getSetting(String key) {
        return getSetting(key, null);
    }

    public Object getSetting(String key, Object defaultValue) {
        if (setting == null) {
            return null;
        }
        return setting.getOrDefault(key, defaultValue);
    }
}
