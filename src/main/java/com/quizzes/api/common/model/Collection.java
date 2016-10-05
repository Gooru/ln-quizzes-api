package com.quizzes.api.common.model;

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
import java.util.UUID;

@TypeDefs({@TypeDef(name = "StringJsonType", typeClass = StringJsonType.class)})

@Entity
public class Collection {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @Column(name = "id")
    @Type(type = "pg-uuid")
    private UUID id;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "is_collection")
    private boolean isCollection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_profile_id")
    private Profile owner;

    @Column(name = "lms_id")
    private String lmsId;

    @Column(name = "collection_body", columnDefinition = "jsonb")
    @Type(type = "StringJsonType")
    private String collectionBody;

    @Column(name = "is_deleted")
    private boolean isDeleted;


    public Collection() {
    }

    public Collection(String externalId, Profile owner) {
        this.externalId = externalId;
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setIsCollection(boolean isCollection) {
        this.isCollection = isCollection;
    }

    public Profile getOwner() {
        return owner;
    }

    public void setOwner(Profile owner) {
        this.owner = owner;
    }

    public String getLmsId() {
        return lmsId;
    }

    public void setLmsId(String lmsId) {
        this.lmsId = lmsId;
    }

    public String getCollectionBody() {
        return collectionBody;
    }

    public void setCollectionBody(String collectionBody) {
        this.collectionBody = collectionBody;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
