package com.quizzes.api.core.exceptions;

import java.util.ArrayList;

public class MissingJsonPropertiesException extends RuntimeException {

    private static final String MISSING_JSON_PROPERTIES = "Missing JSON properties: %s";

    public MissingJsonPropertiesException(ArrayList<String> params) {
        super(String.format(MISSING_JSON_PROPERTIES, String.join(", ", params)));
    }

    public MissingJsonPropertiesException(String param) {
        super(String.format(MISSING_JSON_PROPERTIES, param));
    }

}
