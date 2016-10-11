package com.quizzes.api.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizzes.api.common.exception.MissingJsonPropertiesException;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

public class JsonUtil {


    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Returns an ArrayList with the missing properties.
     *
     * @param json       The Json with the properties
     * @param properties The list of strings to be used for the search
     * @return the list of properties missing if it exists
     */
    public ArrayList<String> getMissingPropertiesList(Map<String, Object> json, String[] properties) {
        ArrayList<String> missingParamsList = new ArrayList<String>();
        for (String jsonProperty : properties) {
            if (!json.containsKey(jsonProperty)) {
                missingParamsList.add(jsonProperty);
            }
        }
        if (!missingParamsList.isEmpty()) {
            throw new MissingJsonPropertiesException(missingParamsList);
        }
        return missingParamsList;
    }

    /**
     * Returns a Map with the children of the property.
     *
     * @param json     The Json where the method is going to search for the property
     * @param property The string to be used for the search
     * @return the property if it exists
     */
    public Map<String, Object> getJsonChildrenByProperty(Map<String, Object> json, String property) {
        Map<String, Object> result = (Map<String, Object>) json.get(property);
        if (result == null) {
            throw new MissingJsonPropertiesException(property);
        }
        return result;
    }

}

