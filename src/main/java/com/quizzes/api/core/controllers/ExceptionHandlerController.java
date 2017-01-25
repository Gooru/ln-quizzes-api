package com.quizzes.api.core.controllers;

import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.dtos.ExceptionMessageDto;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidCredentialsException;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.exceptions.InvalidSessionException;
import com.quizzes.api.core.exceptions.MissingJsonPropertiesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class ExceptionHandlerController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Handles exceptions with incorrect properties in json
     *
     * @return Missing properties and status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MissingJsonPropertiesException.class)
    public ExceptionMessageDto handleInvalidJsonPropertiesException(MissingJsonPropertiesException e) {
        logger.error("Bad request. Invalid JSON", e);
        return new ExceptionMessageDto("Invalid JSON", HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * Handles exceptions when content was not found
     *
     * @return Content Not Found and status 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ContentNotFoundException.class)
    public ExceptionMessageDto handleContentNotFoundException(ContentNotFoundException e) {
        logger.error("Content not found", e);
        return new ExceptionMessageDto("Content not found", HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    /**
     * Handles exceptions when client credentials are wrong
     *
     * @return Invalid Credentials and status 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = InvalidCredentialsException.class)
    public ExceptionMessageDto handleInvalidCredentialsException(InvalidCredentialsException e) {
        logger.error("Invalid credentials", e);
        return new ExceptionMessageDto("Invalid credentials", HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    /**
     * Handles invalid session errors
     *
     * @return Invalid Session and status 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = InvalidSessionException.class)
    public ExceptionMessageDto handleInvalidSessionException(InvalidSessionException e) {
        logger.error("Invalid Session", e);
        return new ExceptionMessageDto("Invalid Session", HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    /**
     * Handles Invalid Owner exception scenarios
     *
     * @return Forbidden Entity error with Status 403
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = InvalidOwnerException.class)
    public ExceptionMessageDto handleInvalidOwnerException(InvalidOwnerException e) {
        logger.error("The Owner is invalid", e);
        return new ExceptionMessageDto("The Owner is invalid", HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    /**
     * Handles any general exception
     *
     * @return Exception message with status code 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ExceptionMessageDto handleException(Exception e) {
        logger.error("Internal Server Error", e);
        return new ExceptionMessageDto("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    /**
     * Handles forbidden request errors
     *
     * @return Exception message
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = InvalidAssigneeException.class)
    public ExceptionMessageDto handleInvalidAssigneeException(InvalidAssigneeException e) {
        logger.error("Forbidden request", e);
        return new ExceptionMessageDto("Forbidden request", HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    /**
     * Handles invalid request errors
     *
     * @return Invalid request and status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InvalidRequestException.class)
    public ExceptionMessageDto handleInvalidRequestException(InvalidRequestException e) {
        logger.error("Invalid request", e);
        return new ExceptionMessageDto("Invalid request", HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

}