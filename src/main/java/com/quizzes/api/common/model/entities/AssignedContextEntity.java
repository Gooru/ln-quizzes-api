package com.quizzes.api.common.model.entities;

import java.util.UUID;

public interface AssignedContextEntity {
    UUID getCollectionId();

    void setCollectionId(UUID collectionId);

    UUID getContextId();

    void setContextId(UUID contextId);

    UUID getProfileId();

    void setProfileId(UUID profileId);

    String getProfileData();

    void setProfileData(String profileData);

    String getContextData();

    void setContextData(String contextData);

}

//public class AssignedContextEntity {
//
//    private Context context;
//    private Profile owner;
//
//
//    public Context getContext() {
//        return context;
//    }
//
//    public void setContext(Context context) {
//        this.context = context;
//    }
//
//    public Profile getOwner() {
//        return owner;
//    }
//
//    public void setOwner(Profile owner) {
//        this.owner = owner;
//    }
//}
