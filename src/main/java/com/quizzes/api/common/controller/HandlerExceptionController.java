package com.quizzes.api.common.controller;

import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.ExceptionMessage;
import com.quizzes.api.common.exception.InvalidCredentialsException;
import com.quizzes.api.common.exception.InvalidOwnerException;
import com.quizzes.api.common.exception.InvalidSessionException;
import com.quizzes.api.common.exception.MissingJsonPropertiesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class HandlerExceptionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        logger.error("Content not found", e);
        return new ExceptionMessage(e.getMessage(), HttpStatus.NOT_FOUND.value(),
                ContentNotFoundException.class.getSimpleName());
    }

    /**
     * Handles exceptions when client credentials are wrong
     *
     * @return Invalid Credentials and status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InvalidCredentialsException.class)
    public ExceptionMessage handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new ExceptionMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value(),
                InvalidCredentialsException.class.getSimpleName());
    }

    /**
     * Handles invalid session errors
     *
     * @return Invalid Session and status 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = InvalidSessionException.class)
    public ExceptionMessage handleInvalidSessionException(InvalidSessionException e) {
        logger.error("Invalid Session", e);
        return new ExceptionMessage("Invalid Session", HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    /**
     * Handles Invalid Owner exception scenarios
     *
     * @return Unprocessable Entity error with Status 422
     */
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(value = InvalidOwnerException.class)
    public ExceptionMessage handleInvalidOwnerException(InvalidOwnerException e) {
        logger.error("The Owner is invalid", e);
        return new ExceptionMessage("The Owner is invalid", HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage());
    }

    /**
     * Handles any general exception
     *
     * @return Exception message with status code 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ExceptionMessage handleException(Exception e) {
        logger.error("Internal Server Error", e);
        return new ExceptionMessage("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

}