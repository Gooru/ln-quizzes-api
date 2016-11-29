package com.quizzes.api.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil {

    @Autowired
    Gson gson;

    public JsonObject removePropertyFromObject(Object object, String propertyToRemove){
        if(propertyToRemove != null) {
            JsonElement jsonElement = gson.toJsonTree(object);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            jsonObject.remove(propertyToRemove);
            return jsonObject;
        }
        throw new IllegalArgumentException("Cannot remove property null");
    }
}
