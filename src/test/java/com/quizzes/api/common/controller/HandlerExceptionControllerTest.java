package com.quizzes.api.common.controller;

import com.quizzes.api.common.exception.ExceptionMessage;
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
        assertEquals("Wrong exception", "MissingJsonPropertiesException", result.getException());
        assertEquals("Wrong status code", HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Wrong message exception", "Missing JSON properties: classId, unitId", result.getMessage());
    }

    @Test
    public void handleException() throws Exception {
        Exception exceptionMock = new Exception("New Error");
        ExceptionMessage result = controller.handleException(exceptionMock);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong exception", "Exception", result.getException());
        assertEquals("Wrong status code", HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Wrong message exception", "New Error", result.getMessage());
    }

}