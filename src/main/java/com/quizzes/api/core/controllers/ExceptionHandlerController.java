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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
        return getExceptionMessageDto("Bad request. Invalid JSON", e, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions when content was not found
     *
     * @return Content Not Found and status 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ContentNotFoundException.class)
    public ExceptionMessageDto handleContentNotFoundException(ContentNotFoundException e) {
        return getExceptionMessageDto("Content not found", e, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions when client credentials are wrong
     *
     * @return Invalid Credentials and status 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = InvalidCredentialsException.class)
    public ExceptionMessageDto handleInvalidCredentialsException(InvalidCredentialsException e) {
        return getExceptionMessageDto("Invalid credentials", e, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles invalid session errors
     *
     * @return Invalid Session and status 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = InvalidSessionException.class)
    public ExceptionMessageDto handleInvalidSessionException(InvalidSessionException e) {
        return getExceptionMessageDto("Invalid Session", e, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles Invalid Owner exception scenarios
     *
     * @return Forbidden Entity error with Status 403
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = InvalidOwnerException.class)
    public ExceptionMessageDto handleInvalidOwnerException(InvalidOwnerException e) {
        return getExceptionMessageDto("The Owner is invalid", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles any general exception
     *
     * @return Exception message with status code 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ExceptionMessageDto handleException(Exception e) {
        return getExceptionMessageDto("Internal Server Error", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles forbidden request errors
     *
     * @return Exception message
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = InvalidAssigneeException.class)
    public ExceptionMessageDto handleInvalidAssigneeException(InvalidAssigneeException e) {
        return getExceptionMessageDto("Forbidden request", e, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles invalid request errors
     *
     * @return Invalid request and status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InvalidRequestException.class)
    public ExceptionMessageDto handleInvalidRequestException(InvalidRequestException e) {
        return getExceptionMessageDto("Invalid request", e, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Spring MethodArgumentTypeMismatchException
     *
     * @return Bad Request with status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ExceptionMessageDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return getExceptionMessageDto("Bad Request", e, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Spring MissingServletRequestParameterException
     *
     * @return Bad Request with status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ExceptionMessageDto handleMissingRequestParameterException(MissingServletRequestParameterException e) {
        return getExceptionMessageDto("Bad Request", e, HttpStatus.BAD_REQUEST);
    }

    private ExceptionMessageDto getExceptionMessageDto(String message, Exception exception, HttpStatus status) {
        logger.error(message, exception);
        return new ExceptionMessageDto(message, status.value(), exception.getMessage());
    }

}