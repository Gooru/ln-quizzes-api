package com.quizzes.api.common.exception;

import java.util.ArrayList;

public class MissingJsonPropertiesException extends RuntimeException {

    public MissingJsonPropertiesException(ArrayList<String> params) {
        super(String.format(ExceptionMessageTemplate.MISSING_JSON_PROPERTIES, String.join(", ", params)));
    }

    public MissingJsonPropertiesException(String param) {
        super(String.format(ExceptionMessageTemplate.MISSING_JSON_PROPERTIES, param));
    }

}
