package com.quizzes.api.core.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SessionService.class)
public class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService = spy(new SessionService());

    @Before
    public void beforeEachTest() {
    }

    @Test
    public void nothing() {

    }

}