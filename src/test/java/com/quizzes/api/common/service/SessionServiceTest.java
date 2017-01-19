package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.SessionPostRequestDto;
import com.quizzes.api.common.dto.SessionTokenDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.InvalidSessionException;
import com.quizzes.api.common.model.entities.SessionProfileEntity;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.model.jooq.tables.pojos.Session;
import com.quizzes.api.common.repository.SessionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

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

    @Mock
    private Gson gson = new Gson();

    private String apiKey;
    private String apiSecret;
    private UUID sessionId;
    private UUID profileId;
    private UUID clientId;
    private String externalUserId;
    private Lms gooruLms;

    @Before
    public void beforeEachTest() {
        apiKey = UUID.randomUUID().toString();
        apiSecret = UUID.randomUUID().toString();
        sessionId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        externalUserId = UUID.randomUUID().toString();
        gooruLms = Lms.gooru;
    }

    @Test
    public void generateTokenWhenProfileIsNull() throws Exception {
        SessionPostRequestDto sessionPostRequestDto = createSessionPostRequestDto();
        Client client = createClient();

        when(clientService.findByApiKeyAndApiSecret(apiKey, apiSecret)).thenReturn(client);
        when(profileService.findIdByExternalIdAndClientId(externalUserId, clientId))
                .thenThrow(ContentNotFoundException.class);
        when(profileService.saveProfileBasedOnExternalUser(sessionPostRequestDto.getUser(), gooruLms, clientId))
                .thenReturn(createProfile());
        when(sessionRepository.save(any(Session.class))).thenReturn(createSession());

        SessionTokenDto result = sessionService.generateToken(sessionPostRequestDto);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey), eq(apiSecret));
        verify(profileService, times(1)).findIdByExternalIdAndClientId(eq(externalUserId), eq(clientId));
        verify(profileService, times(1))
                .saveProfileBasedOnExternalUser(eq(sessionPostRequestDto.getUser()), eq(gooruLms), eq(clientId));
        verify(sessionRepository, times(0)).findLastSessionByProfileId(any());
        verify(sessionRepository, times(0)).updateLastAccess(any());
        verify(sessionRepository, times(1)).save(any(Session.class));

        assertNotNull("Result is null", result);
        assertEquals("Wrong session Id", sessionId, result.getSessionToken());
    }

    @Test
    public void generateTokenWhenLastSessionIsNull() throws Exception {
        SessionPostRequestDto sessionPostRequestDto = createSessionPostRequestDto();
        Client client = createClient();

        when(clientService.findByApiKeyAndApiSecret(apiKey, apiSecret)).thenReturn(client);
        when(profileService.findIdByExternalIdAndClientId(externalUserId, clientId)).thenReturn(profileId);
        when(sessionRepository.findLastSessionByProfileId(profileId)).thenThrow(ContentNotFoundException.class);
        when(sessionRepository.save(any(Session.class))).thenReturn(createSession());

        SessionTokenDto result = sessionService.generateToken(sessionPostRequestDto);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey), eq(apiSecret));
        verify(profileService, times(1)).findIdByExternalIdAndClientId(eq(externalUserId), eq(clientId));
        verify(profileService, times(0)).saveProfileBasedOnExternalUser(any(), any(), any());
        verify(sessionRepository, times(1)).findLastSessionByProfileId(eq(profileId));
        verify(sessionRepository, times(0)).updateLastAccess(any());
        verify(sessionRepository, times(1)).save(any(Session.class));

        assertNotNull("Result is null", result);
        assertEquals("Wrong session Id", sessionId, result.getSessionToken());
    }

    @Test
    public void generateTokenWhenLastSessionIsNotNull() throws Exception {
        SessionPostRequestDto sessionPostRequestDto = createSessionPostRequestDto();
        Session session = createSession();
        Client client = createClient();

        when(clientService.findByApiKeyAndApiSecret(apiKey, apiSecret)).thenReturn(client);
        when(profileService.findIdByExternalIdAndClientId(externalUserId, clientId)).thenReturn(profileId);
        when(sessionRepository.findLastSessionByProfileId(profileId)).thenReturn(session);
        when(sessionRepository.updateLastAccess(sessionId)).thenReturn(session);

        SessionTokenDto result = sessionService.generateToken(sessionPostRequestDto);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey), eq(apiSecret));
        verify(profileService, times(1)).findIdByExternalIdAndClientId(eq(externalUserId), eq(clientId));
        verify(profileService, times(0)).saveProfileBasedOnExternalUser(any(), any(), any());
        verify(sessionRepository, times(1)).findLastSessionByProfileId(eq(profileId));
        verify(sessionRepository, times(1)).updateLastAccess(eq(sessionId));
        verify(sessionRepository, times(0)).save(any());

        assertNotNull("Result is null", result);
        assertEquals("Wrong session Id", sessionId, result.getSessionToken());
    }

    @Test
    public void save() throws Exception {
        Session session = createSession();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionRepository.save(session);
        verify(sessionRepository, times(1)).save(eq(session));
        assertEquals("Wrong session ID", sessionId, result.getId());
        assertEquals("Wrong profile ID", profileId, result.getProfileId());
    }

    @Test
    public void getSessionToken() throws Exception {
        SessionTokenDto result = WhiteboxImpl.invokeMethod(sessionService, "getSessionToken", createSession());

        assertNotNull("Result is null", result);
        assertEquals("Wrong session token", sessionId, result.getSessionToken());
    }

    @Test
    public void findProfileBySessionId() throws Exception {
        Profile profile = createProfile();

        when(sessionRepository.findProfileBySessionId(sessionId)).thenReturn(profile);

        Profile result = WhiteboxImpl.invokeMethod(sessionService, "findProfileBySessionId", sessionId);

        assertNotNull("Result is null", result);
        assertEquals("Wrong profile id", profileId, result.getId());
        assertEquals("Wrong profile external id", externalUserId, result.getExternalId());
        assertEquals("Wrong profile data", profile.getProfileData(), result.getProfileData());
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
        sessionService.findSessionProfileEntityBySessionId(sessionId);
    }

    @Test
    public void isSessionAliveReturnsTrue() {
        Timestamp lastAccess = Timestamp.valueOf("2007-09-23 10:05:10.0");
        Timestamp current = Timestamp.valueOf("2007-09-23 10:10:10.0");

        when(configurationService.getSessionMinutes()).thenReturn(Double.valueOf(360));

        boolean result = sessionService.isSessionAlive(sessionId, lastAccess, current);
        verify(configurationService, times(1)).getSessionMinutes();
        assertTrue("Result is false", result);
    }

    @Test
    public void isSessionAliveReturnsFalse() {
        Timestamp lastAccess = Timestamp.valueOf("2007-09-23 10:05:10.0");
        Timestamp current = Timestamp.valueOf("2007-09-23 10:10:10.0");

        when(configurationService.getSessionMinutes()).thenReturn(Double.valueOf(2));

        boolean result = sessionService.isSessionAlive(sessionId, lastAccess, current);
        verify(configurationService, times(1)).getSessionMinutes();
        assertFalse("Result is true", result);
    }

    @Test
    public void findLastSessionByProfileId() throws Exception {
        when(sessionRepository.findLastSessionByProfileId(profileId)).thenReturn(createSession());

        Session result = sessionService.findLastSessionByProfileId(profileId);

        verify(sessionRepository, times(1)).findLastSessionByProfileId(eq(profileId));
        assertEquals("Wrong session ID", sessionId, result.getId());
        assertEquals("Wrong profile ID", profileId, result.getProfileId());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findLastSessionByProfileIdThrowsException() throws Exception {
        when(sessionRepository.findLastSessionByProfileId(profileId)).thenReturn(null);
        sessionService.findLastSessionByProfileId(profileId);
    }

    private Session createSession() {
        Session session = new Session();
        session.setId(sessionId);
        session.setProfileId(profileId);
        return session;
    }

    private Client createClient() {
        Client client = new Client();
        client.setId(clientId);
        return client;
    }

    private ExternalUserDto createExternalUserDto() {
        ExternalUserDto userDto = new ExternalUserDto();
        userDto.setExternalId(externalUserId);
        userDto.setFirstName("firstName");
        userDto.setLastName("lastName");
        userDto.setUsername("username");
        return userDto;
    }

    private SessionPostRequestDto createSessionPostRequestDto() {
        SessionPostRequestDto sessionPostRequestDto = new SessionPostRequestDto();
        sessionPostRequestDto.setClientApiKey(apiKey);
        sessionPostRequestDto.setClientApiSecret(apiSecret);
        sessionPostRequestDto.setUser(createExternalUserDto());
        return sessionPostRequestDto;
    }

    private Profile createProfile() {
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setExternalId(externalUserId);
        profile.setProfileData(gson.toJson(createExternalUserDto()));
        return profile;
    }
}