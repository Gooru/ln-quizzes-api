package com.quizzes.api.common.dto;

import com.quizzes.api.common.dto.controller.CollectionDto;

import java.util.Map;
import java.util.UUID;

public class CommonContextGetResponseDto {

    private UUID id;

    private CollectionDto collection;

    private long createdDate;

    private transient ContextDataDto contextData;

    public CommonContextGetResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CollectionDto getCollection() {
        return collection;
    }

    public void setCollection(CollectionDto collection) {
        this.collection = collection;
    }

    public long getCreatedDate(){
        return this.createdDate;
    }

    public void setCreatedDate(long createdDate){
      this.createdDate = createdDate;
    }

    public ContextDataDto getContextData() {
        return contextData;
    }

    public void setContextData(ContextDataDto contextData) {
        this.contextData = contextData;
    }

    public static class ContextDataDto {

        private Map<String, String> metadata;
        private Map<String, String> contextMap;

        public ContextDataDto() {
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }

        public Map<String, String> getContextMap() {
            return contextMap;
        }

        public void setContextMap(Map<String, String> contextMap) {
            this.contextMap = contextMap;
        }
    }
}
