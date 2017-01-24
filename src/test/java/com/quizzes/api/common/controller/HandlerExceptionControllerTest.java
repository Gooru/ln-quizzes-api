package com.quizzes.api.common.controller;

import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.ExceptionMessage;
import com.quizzes.api.common.exception.InvalidAssigneeException;
import com.quizzes.api.common.exception.InvalidCredentialsException;
import com.quizzes.api.common.exception.InvalidOwnerException;
import com.quizzes.api.common.exception.InvalidRequestException;
import com.quizzes.api.common.exception.InvalidSessionException;
import com.quizzes.api.common.exception.MissingJsonPropertiesException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HandlerExceptionControllerTest {

    @InjectMocks
    private HandlerExceptionController controller = new HandlerExceptionController();

    @Test
    public void handleInvalidJsonPropertiesException() throws Exception {
        ArrayList<String> missingParams = new ArrayList<>();
        missingParams.add("classId");
        missingParams.add("unitId");

        MissingJsonPropertiesException exceptionMock = new MissingJsonPropertiesException(missingParams);
        ExceptionMessage result = controller.handleInvalidJsonPropertiesException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Missing JSON properties: classId, unitId", result.getException());
        assertEquals("Wrong status code", HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Wrong message exception", "Invalid JSON", result.getMessage());
    }

    @Test
    public void handleException() throws Exception {
        Exception exceptionMock = new Exception("New Error");
        ExceptionMessage exceptionMessage = controller.handleException(exceptionMock);
        assertNotNull("Exception Message is null", exceptionMessage);
        assertEquals("Wrong message exception", "Internal Server Error" , exceptionMessage.getMessage());
        assertEquals("Wrong status code", HttpStatus.INTERNAL_SERVER_ERROR.value(), exceptionMessage.getStatus());
        assertEquals("Wrong exception", "New Error", exceptionMessage.getException());
    }

    @Test
    public void handleContentNotFoundException() throws Exception {
        ContentNotFoundException exceptionMock = new ContentNotFoundException("We couldn't find the param");
        ExceptionMessage result = controller.handleContentNotFoundException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "We couldn't find the param", result.getException());
        assertEquals("Wrong status code", HttpStatus.NOT_FOUND.value(), result.getStatus());
        assertEquals("Wrong message exception", "Content not found", result.getMessage());
    }

    @Test
    public void handleInvalidCredentialsException() throws Exception {
        InvalidCredentialsException exceptionMock = new InvalidCredentialsException("Invalid client credentials.");
        ExceptionMessage result = controller.handleInvalidCredentialsException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid client credentials.", result.getException());
        assertEquals("Wrong status code", HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Wrong message exception", "Invalid credentials", result.getMessage());
    }

    @Test
    public void handleInvalidSessionException() throws Exception {
        InvalidSessionException exceptionMock = new InvalidSessionException("Invalid session message");
        ExceptionMessage result = controller.handleInvalidSessionException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid session message", result.getException());
        assertEquals("Wrong status code", HttpStatus.UNAUTHORIZED.value(), result.getStatus());
        assertEquals("Wrong message exception", "Invalid Session", result.getMessage());
    }

    @Test
    public void handleInvalidOwnerException() throws Exception {
        InvalidOwnerException exceptionMock = new InvalidOwnerException("Invalid session message");
        ExceptionMessage result = controller.handleInvalidOwnerException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid session message", result.getException());
        assertEquals("Wrong status code", HttpStatus.FORBIDDEN.value(), result.getStatus());
        assertEquals("Wrong message exception", "The Owner is invalid", result.getMessage());
    }

    @Test
    public void handleInvalidAssigneeException() throws Exception {
        InvalidAssigneeException exceptionMock = new InvalidAssigneeException("Invalid assignee request");
        ExceptionMessage result = controller.handleInvalidAssigneeException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid assignee request", result.getException());
        assertEquals("Wrong status code", HttpStatus.FORBIDDEN.value(), result.getStatus());
        assertEquals("Wrong message exception", "Forbidden request", result.getMessage());
    }

    @Test
    public void handleInvalidRequestException() throws Exception {
        InvalidRequestException exceptionMock = new InvalidRequestException("Invalid request arguments");
        ExceptionMessage result = controller.handleInvalidRequestException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Invalid request arguments", result.getException());
        assertEquals("Wrong status code", HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Wrong message exception", "Invalid request", result.getMessage());
    }
}