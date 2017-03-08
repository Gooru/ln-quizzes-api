package com.quizzes.api.core.dtos;

import com.quizzes.api.core.enums.CollectionSetting;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class CollectionMetadataDto implements Serializable {

    private String title;
    private Map<String, Object> setting;
    private Map<String, Object> taxonomy;

    public Object getSetting(CollectionSetting key) {
        return getSetting(key, null);
    }

    public Object getSetting(CollectionSetting key, Object defaultValue) {
        if (setting == null) {
            return defaultValue;
        }
        return setting.getOrDefault(key.getLiteral(), defaultValue);
    }
}
