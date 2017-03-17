package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.ExceptionMessageDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidClassMemberException;
import com.quizzes.api.core.exceptions.InvalidCredentialsException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.exceptions.InvalidRequestBodyException;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.exceptions.InvalidSessionException;
import com.quizzes.api.core.exceptions.MissingJsonPropertiesException;
import com.quizzes.api.core.exceptions.NoAttemptsLeftException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
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
        return getExceptionMessageDto("Invalid JSON", HttpStatus.BAD_REQUEST, e);
    }

    /**
     * Handles exceptions when content was not found
     *
     * @return Content Not Found and status 404
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ContentNotFoundException.class)
    public ExceptionMessageDto handleContentNotFoundException(ContentNotFoundException e) {
        return getExceptionMessageDto("Content not found", HttpStatus.NOT_FOUND, e);
    }

    /**
     * Handles exceptions when client credentials are wrong
     *
     * @return Invalid Credentials and status 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = InvalidCredentialsException.class)
    public ExceptionMessageDto handleInvalidCredentialsException(InvalidCredentialsException e) {
        return getExceptionMessageDto("Invalid credentials", HttpStatus.UNAUTHORIZED, e);
    }

    /**
     * Handles invalid session errors
     *
     * @return Invalid Session and status 401
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = InvalidSessionException.class)
    public ExceptionMessageDto handleInvalidSessionException(InvalidSessionException e) {
        return getExceptionMessageDto("Invalid Session", HttpStatus.UNAUTHORIZED, e);
    }

    /**
     * Handles Invalid Owner exception scenarios
     *
     * @return Forbidden Entity error with Status 403
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = InvalidOwnerException.class)
    public ExceptionMessageDto handleInvalidOwnerException(InvalidOwnerException e) {
        return getExceptionMessageDto("The Owner is invalid", HttpStatus.FORBIDDEN, e);
    }

    /**
     * Handles forbidden request errors
     *
     * @return Exception message
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = InvalidAssigneeException.class)
    public ExceptionMessageDto handleInvalidAssigneeException(InvalidAssigneeException e) {
        return getExceptionMessageDto("Forbidden request", HttpStatus.FORBIDDEN, e);
    }

    /**
     * Handles forbidden request errors
     *
     * @return Exception message
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = InvalidClassMemberException.class)
    public ExceptionMessageDto handleInvalidClassMemberException(InvalidClassMemberException e) {
        return getExceptionMessageDto("Forbidden request", HttpStatus.FORBIDDEN, e);
    }

    /**
     * Handles invalid request errors
     *
     * @return Invalid request and status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InvalidRequestException.class)
    public ExceptionMessageDto handleInvalidRequestException(InvalidRequestException e) {
        return getExceptionMessageDto("Invalid request", HttpStatus.BAD_REQUEST, e);
    }

    /**
     * Handles Spring MethodArgumentTypeMismatchException
     *
     * @return Bad Request with status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ExceptionMessageDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return getExceptionMessageDto("Bad Request", HttpStatus.BAD_REQUEST, e);
    }

    /**
     * Handles Spring MissingServletRequestParameterException
     *
     * @return Bad Request with status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ExceptionMessageDto handleMissingRequestParameterException(MissingServletRequestParameterException e) {
        return getExceptionMessageDto("Bad Request", HttpStatus.BAD_REQUEST, e);
    }

    /**
     * Handles Spring IllegalArgumentException
     *
     * @return Bad Request with status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ExceptionMessageDto handleIllegalArgumentException(IllegalArgumentException e) {
        return getExceptionMessageDto("Bad Request", HttpStatus.BAD_REQUEST, e);
    }

    /**
     * Handles InvalidRequestBodyException
     *
     * @return Bad Request with status 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InvalidRequestBodyException.class)
    public ExceptionMessageDto handleInvalidRequestBodyException(InvalidRequestBodyException e) {
        return getExceptionMessageDto("Invalid request body", HttpStatus.BAD_REQUEST, e);
    }

    /**
     * Handles NoAttemptsLeftException
     *
     * @return Unprocessable entity with status 422
     */
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(value = NoAttemptsLeftException.class)
    public ExceptionMessageDto handleNoAttemptsLeftException(NoAttemptsLeftException e) {
        return getExceptionMessageDto("Validation error. No attempts left", HttpStatus.UNPROCESSABLE_ENTITY, e);
    }

    /**
     * Handles any SQL Exception
     *
     * @return Exception message with status code 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = DataAccessException.class)
    public ExceptionMessageDto handleException(DataAccessException e) {
        logger.error("Internal Server Error", e);
        return new ExceptionMessageDto("Internal Server Error",  HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Database exception has occurred");
    }

    /**
     * Handles the explicit InternalServerException
     *
     * @return Exception message with status code 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = InternalServerException.class)
    public ExceptionMessageDto handleException(InternalServerException e) {
        return getExceptionMessageDto("Server processing error", HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    /**
     * Handles any other exception
     *
     * @return Exception message with status code 500
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ExceptionMessageDto handleException(Exception e) {
        return getExceptionMessageDto("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    private ExceptionMessageDto getExceptionMessageDto(String message, HttpStatus status, Exception exception) {
        logger.error(message, exception);
        return new ExceptionMessageDto(message, status.value(), exception.getMessage());
    }

}