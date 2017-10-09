package com.quizzes.api.core.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class ClassMemberContentDto {

    @SerializedName("member")
    private List<UUID> memberIds;

    @SerializedName("owner")
    private List<UUID> ownerIds;

    @SerializedName("collaborator")
    private List<UUID> collaborators;

    public List<UUID> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<UUID> memberIds) {
        this.memberIds = memberIds;
    }

    public void setOwnerIds(List<UUID> ownerIds) {
        this.ownerIds = ownerIds;
    }

    public List<UUID> getOwnerIds() {
        return ownerIds;
    }

    public List<UUID> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<UUID> collaborators) {
        this.collaborators = collaborators;
    }
}
