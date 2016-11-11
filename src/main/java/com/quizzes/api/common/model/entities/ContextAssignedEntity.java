package com.quizzes.api.common.model.entities;

import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.Profile;

public class ContextAssignedEntity {

    private Context context;
    private Profile owner;


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Profile getOwner() {
        return owner;
    }

    public void setOwner(Profile owner) {
        this.owner = owner;
    }
}
