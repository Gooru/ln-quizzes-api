package com.quizzes.api.core.services;

import com.quizzes.api.core.dtos.SessionTokenDto;
import com.quizzes.api.core.model.jooq.tables.pojos.Session;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SessionService.class)
public class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService = spy(new SessionService());


    private UUID sessionId;
    private UUID profileId;

    @Before
    public void beforeEachTest() {
        sessionId = UUID.randomUUID();
        profileId = UUID.randomUUID();
    }

    @Ignore
    @Test
    public void createSessionPrivateMethod() throws Exception {
        doReturn(createSession()).when(sessionService, "save", any(Session.class));

        Session result =
                WhiteboxImpl.invokeMethod(sessionService, "createSession", profileId);

        verifyPrivate(sessionService, times(1)).invoke("save", any(Session.class));
        assertEquals("Wrong profileId", profileId, result.getProfileId());
        assertEquals("Wrong sessionId", sessionId, result.getId());
    }

    @Ignore
    @Test
    public void getSessionToken() throws Exception {
        SessionTokenDto result = WhiteboxImpl.invokeMethod(sessionService, "getSessionToken", createSession());

        assertNotNull("Result is null", result);
        assertEquals("Wrong session token", sessionId, result.getSessionToken());
    }

    private Session createSession() {
        Session session = new Session();
        session.setId(sessionId);
        session.setProfileId(profileId);
        return session;
    }

}