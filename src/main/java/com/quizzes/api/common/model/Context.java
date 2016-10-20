package com.quizzes.api.common.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@TypeDefs({@TypeDef(name = "StringJsonType", typeClass = StringJsonType.class)})

@Entity
public class Context {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @Column(name = "id", nullable = false)
    @JsonProperty("contextId")
    @Type(type = "pg-uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @Column(name = "context_body", columnDefinition = "jsonb")
    @Type(type = "StringJsonType")
    private String contextBody;

    public Context() {
    }

    public Context(Collection collection) {
        this.collection = collection;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getContextBody() {
        return contextBody;
    }

    public void setContextBody(String contextBody) {
        this.contextBody = contextBody;
    }

}

