package com.quizzes.api.common.controller;

import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.ExceptionMessage;
import com.quizzes.api.common.exception.MissingJsonPropertiesException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class HandlerExceptionController {

    /**
     * Handles exceptions with incorrect properties in json
     *
     * @return Missing properties and status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MissingJsonPropertiesException.class)
    public ExceptionMessage handleInvalidJsonPropertiesException(MissingJsonPropertiesException e) {
        return new ExceptionMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value(),
                MissingJsonPropertiesException.class.getSimpleName());
    }

    /**
     * Handles exceptions when content was not found
     *
     * @return Content Not Found and status 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ContentNotFoundException.class)
    public ExceptionMessage handleContentNotFoundException(ContentNotFoundException e) {
        return new ExceptionMessage(e.getMessage(), HttpStatus.NOT_FOUND.value(),
                ContentNotFoundException.class.getSimpleName());
    }

    /**
     * Handles basic exceptions
     *
     * @return Message exception and status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public ExceptionMessage handleException(Exception e) {
        return new ExceptionMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value(), Exception.class.getSimpleName());
    }

}