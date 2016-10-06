package com.quizzes.api.common.model;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@TypeDefs({@TypeDef(name = "StringJsonType", typeClass = StringJsonType.class)})

@Entity
public class Profile {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @Column(name = "id")
    @Type(type = "pg-uuid")
    private UUID id;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "profile_body", columnDefinition = "jsonb")
    @Type(type = "StringJsonType")
    private String profileBody;

    public Profile() {
    }

    public Profile(String externalId) {
        this.externalId = externalId;
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

    public String getProfileBody() {
        return profileBody;
    }

    public void setProfileBody(String profileBody) {
        this.profileBody = profileBody;
    }

}
