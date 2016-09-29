package com.quizzes.api.realtime.model;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.Entity;
import java.util.UUID;

@Entity(name = "collection_on_air")
public class CollectionOnAir {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @org.hibernate.annotations.Type(type="pg-uuid")
    private UUID id;
    private String classId;
    private String collectionId;

    public CollectionOnAir() {
    }

    public CollectionOnAir(String classId, String collectionId) {
        this.classId = classId;
        this.collectionId = collectionId;
    }

    public UUID getId() {
        return id;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
