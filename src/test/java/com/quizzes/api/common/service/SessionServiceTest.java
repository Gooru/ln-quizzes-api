package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.SessionPostRequestDto;
import com.quizzes.api.common.dto.SessionTokenDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.InvalidCredentialsException;
import com.quizzes.api.common.exception.InvalidSessionException;
import com.quizzes.api.common.interceptor.SessionInterceptor;
import com.quizzes.api.common.model.entities.SessionProfileEntity;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.model.jooq.tables.pojos.Session;
import com.quizzes.api.common.repository.SessionRepository;
import com.quizzes.api.content.gooru.rest.AuthenticationRestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService = Mockito.spy(SessionService.class);

    @Mock
    SessionRepository sessionRepository;

    @Mock
    ClientService clientService;

    @Mock
    ConfigurationService configurationService;

    @Mock
    ProfileService profileService;

    private UUID apiKey;
    private UUID apiSecret;
    private UUID sessionId;

    @Before
    public void beforeEachTest() {
        apiKey = UUID.randomUUID();
        apiSecret = UUID.randomUUID();
        sessionId = UUID.randomUUID();
    }

    @Test
    public void generateTokenWhenLastSessionIsNotNull() throws Exception {
        ExternalUserDto userDto = new ExternalUserDto();
        userDto.setExternalId(UUID.randomUUID().toString());
        userDto.setFirstName("Keylor");
        userDto.setLastName("Navas");
        userDto.setUsername("knavas");

        SessionPostRequestDto session = new SessionPostRequestDto();
        session.setClientApiSecret(apiSecret.toString());
        session.setClientApiKey(apiKey.toString());
        session.setUser(userDto);

        //Setting client mock
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);

        when(clientService.findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()))).thenReturn(client);

        //Search profile
        Lms lms = Lms.gooru;
        UUID profileId = UUID.randomUUID();
        when(profileService.findIdByExternalIdAndClientId(eq(userDto.getExternalId()), eq(clientId))).thenReturn(profileId);

        //Setting session
        Session sessionResult = new Session();
        sessionResult.setProfileId(profileId);
        sessionResult.setId(sessionId);

        //Reusing same session as result since we don't have a way to validate the last_access_at
        when(sessionRepository.findLastSessionByProfileId(eq(profileId))).thenReturn(sessionResult);
        when(sessionRepository.updateLastAccess(any(UUID.class))).thenReturn(sessionResult);

        SessionTokenDto result = sessionService.generateToken(session);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()));
        verify(profileService, times(1)).findIdByExternalIdAndClientId(eq(userDto.getExternalId()), eq(clientId));
        verify(profileService, times(0)).saveProfileBasedOnExternalUser(eq(userDto), eq(lms), eq(client.getId()));

        verify(sessionRepository, times(1)).findLastSessionByProfileId(eq(profileId));
        verify(sessionRepository, times(1)).updateLastAccess(any(UUID.class));

        verify(sessionRepository, times(0)).save(any(Session.class));

        assertNotNull("Result is null", result);
        assertEquals("Wrong session Id", sessionId, result.getSessionToken());
    }

    @Test
    public void generateTokenWhenLastSessionIsNull() throws Exception {
        ExternalUserDto userDto = new ExternalUserDto();
        userDto.setExternalId(UUID.randomUUID().toString());
        userDto.setFirstName("Keylor");
        userDto.setLastName("Navas");
        userDto.setUsername("knavas");

        SessionPostRequestDto session = new SessionPostRequestDto();
        session.setClientApiSecret(apiSecret.toString());
        session.setClientApiKey(apiKey.toString());
        session.setUser(userDto);

        //Setting client mock
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);

        when(clientService.findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()))).thenReturn(client);

        //Search profile
        Lms lms = Lms.gooru;
        UUID profileId = UUID.randomUUID();
        when(profileService.findIdByExternalIdAndClientId(eq(userDto.getExternalId()), eq(clientId))).thenReturn(profileId);

        //Last session is null
        when(sessionRepository.findLastSessionByProfileId(eq(profileId))).thenReturn(null);

        //Setting session
        Session sessionResult = new Session();
        sessionResult.setProfileId(profileId);
        sessionResult.setId(sessionId);

        when(sessionRepository.save(any(Session.class))).thenReturn(sessionResult);

        SessionTokenDto result = sessionService.generateToken(session);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()));
        verify(profileService, times(1)).findIdByExternalIdAndClientId(eq(userDto.getExternalId()), eq(clientId));
        verify(profileService, times(0)).saveProfileBasedOnExternalUser(eq(userDto), eq(lms), eq(client.getId()));

        verify(sessionRepository, times(1)).findLastSessionByProfileId(eq(profileId));
        verify(sessionRepository, times(0)).updateLastAccess(any());

        verify(sessionRepository, times(1)).save(any(Session.class));

        assertNotNull("Result is null", result);
        assertEquals("Wrong session Id", sessionId, result.getSessionToken());
    }

    @Test
    public void generateTokenWhenProfileIsNull() throws Exception {
        ExternalUserDto userDto = new ExternalUserDto();
        userDto.setExternalId(UUID.randomUUID().toString());
        userDto.setFirstName("Keylor");
        userDto.setLastName("Navas");
        userDto.setUsername("knavas");

        SessionPostRequestDto session = new SessionPostRequestDto();
        session.setClientApiSecret(apiSecret.toString());
        session.setClientApiKey(apiKey.toString());
        session.setUser(userDto);

        //Setting client mock
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);

        when(clientService.findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()))).thenReturn(client);

        //Search profile, it returns null
        Lms lms = Lms.gooru;
        when(profileService.findIdByExternalIdAndClientId(eq(userDto.getExternalId()), eq(clientId))).thenReturn(null);

        //Setting Profile
        Profile profile = new Profile();
        UUID profileId = UUID.randomUUID();
        profile.setId(profileId);

        when(profileService
                .saveProfileBasedOnExternalUser(eq(userDto), eq(lms), eq(client.getId()))).thenReturn(profile);

        //Setting session
        Session sessionResult = new Session();
        sessionResult.setProfileId(profileId);
        sessionResult.setId(sessionId);

        when(sessionRepository.save(any(Session.class))).thenReturn(sessionResult);

        SessionTokenDto result = sessionService.generateToken(session);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()));
        verify(profileService, times(1)).findIdByExternalIdAndClientId(eq(userDto.getExternalId()), eq(clientId));
        verify(profileService, times(1)).saveProfileBasedOnExternalUser(eq(userDto), eq(lms), eq(client.getId()));

        verify(sessionRepository, times(0)).findLastSessionByProfileId(any());
        verify(sessionRepository, times(0)).updateLastAccess(any());

        verify(sessionRepository, times(1)).save(any(Session.class));

        assertNotNull("Result is null", result);
        assertEquals("Wrong session Id", sessionId, result.getSessionToken());
    }

    @Test(expected = InvalidCredentialsException.class)
    public void generateTokenShouldThrowExceptionWhenClientIsNull() throws Exception {
        ExternalUserDto userDto = new ExternalUserDto();
        userDto.setExternalId(UUID.randomUUID().toString());
        userDto.setFirstName("Keylor");
        userDto.setLastName("Navas");
        userDto.setUsername("knavas");

        SessionPostRequestDto session = new SessionPostRequestDto();
        session.setClientApiSecret(apiSecret.toString());
        session.setClientApiKey(apiKey.toString());
        session.setUser(userDto);

        when(clientService.findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()))).thenReturn(null);
        SessionTokenDto result = sessionService.generateToken(session);
    }

    @Test
    public void save() throws Exception {
        Session session = new Session();
        Session result = sessionRepository.save(session);
        verify(sessionRepository, times(1)).save(eq(session));
    }

    @Test
    public void getSessionToken() throws Exception {
        UUID id = UUID.randomUUID();
        Session session = new Session();
        session.setId(id);

        SessionTokenDto result = WhiteboxImpl.invokeMethod(sessionService, "getSessionToken", session);

        assertNotNull("Result is null", result);
        assertEquals("Wrong session token", id, result.getSessionToken());
    }

    @Test
    public void findProfileBySessionId() throws Exception {
        //Setting return values from db
        UUID id = UUID.randomUUID();
        UUID externalId = UUID.randomUUID();
        String data = "{\"firstName\": \"David\"," +
                "\"lastName\": \"Artavia\"," +
                "\"username\": \"dartavia\"," +
                "\"email\": \"david@quizzes.com\"}";
        Profile profile = new Profile();
        profile.setId(id);
        profile.setExternalId(externalId.toString());
        profile.setProfileData(data);

        when(sessionRepository.findProfileBySessionId(sessionId)).thenReturn(profile);

        Profile result = WhiteboxImpl.invokeMethod(sessionService, "findProfileBySessionId", sessionId);

        assertNotNull("Result is null", result);
        assertEquals("Wrong profile id", profile.getId(), result.getId());
        assertEquals("Wrong profile external id", externalId.toString(), result.getExternalId());
        assertEquals("Wrong profile data", data, result.getProfileData());
    }

    @Test
    public void updateLastAccess() throws Exception {
        Session result = sessionService.updateLastAccess(sessionId);
        verify(sessionRepository, times(1)).updateLastAccess(eq(sessionId));
    }

    @Test
    public void findSessionProfileEntityBySessionId() throws Exception {
        SessionProfileEntity sessionProfileEntity = Mockito.spy(SessionProfileEntity.class);
        when(sessionProfileEntity.getSessionId()).thenReturn(sessionId);

        when(sessionRepository.findSessionProfileEntityBySessionId(sessionId)).thenReturn(sessionProfileEntity);

        SessionProfileEntity result = sessionService.findSessionProfileEntityBySessionId(sessionId);

        verify(sessionRepository, times(1)).findSessionProfileEntityBySessionId(eq(sessionId));
        assertEquals("Wrong Session ID", sessionId, result.getSessionId());
    }

    @Test(expected = InvalidSessionException.class)
    public void findSessionProfileEntityBySessionIdThrowException() throws Exception {
        when(sessionRepository.findSessionProfileEntityBySessionId(sessionId)).thenReturn(null);
        SessionProfileEntity entity = sessionService.findSessionProfileEntityBySessionId(sessionId);
    }

    @Test
    public void isSessionAliveReturnsTrue(){
        Timestamp lastAccess = Timestamp.valueOf("2007-09-23 10:05:10.0");
        Timestamp current = Timestamp.valueOf("2007-09-23 10:10:10.0");

        when(configurationService.getSessionMinutes()).thenReturn(Double.valueOf(360));

        boolean result = sessionService.isSessionAlive(sessionId, lastAccess, current);
        verify(configurationService, times(1)).getSessionMinutes();
        assertTrue("Result is false", result);
    }

    @Test
    public void isSessionAliveReturnsFalse(){
        Timestamp lastAccess = Timestamp.valueOf("2007-09-23 10:05:10.0");
        Timestamp current = Timestamp.valueOf("2007-09-23 10:10:10.0");

        when(configurationService.getSessionMinutes()).thenReturn(Double.valueOf(2));

        boolean result = sessionService.isSessionAlive(sessionId, lastAccess, current);
        verify(configurationService, times(1)).getSessionMinutes();
        assertFalse("Result is true", result);
    }

}