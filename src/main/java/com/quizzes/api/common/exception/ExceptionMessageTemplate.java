package com.quizzes.api.common.exception;

public interface ExceptionMessageTemplate {

    String MISSING_JSON_PROPERTIES = "Missing JSON properties: %s";
    String ERROR_CONNECTING_API = "There was a problem trying to connect to the API";
    String CONTEXT_NOT_FOUND = "We couldn't find %s";

}
