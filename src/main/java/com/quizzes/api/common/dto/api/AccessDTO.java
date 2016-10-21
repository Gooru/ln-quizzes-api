package com.quizzes.api.common.dto.api;

public class AccessDTO {

    private String client_key;
    private String client_id;
    private String grant_type;

    public AccessDTO(String client_key, String client_id, String grant_type) {
        this.client_key = client_key;
        this.client_id = client_id;
        this.grant_type = grant_type;
    }

    public String getClient_key() {
        return client_key;
    }

    public void setClient_key(String client_key) {
        this.client_key = client_key;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }
}
