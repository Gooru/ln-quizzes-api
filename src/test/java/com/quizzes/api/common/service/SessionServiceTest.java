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
import org.powermock.core.classloader.annotations.PrepareForTest;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SessionService.class)
public class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService = spy(new SessionService());

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private ProfileService profileService;

    @Mock
    private Gson gson = new Gson();

    private String apiKey;
    private String apiSecret;
    private UUID sessionId;
    private UUID profileId;
    private UUID clientId;
    private UUID sessionToken;
    private String externalUserId;
    private Lms gooruLms;

    @Before
    public void beforeEachTest() {
        apiKey = UUID.randomUUID().toString();
        apiSecret = UUID.randomUUID().toString();
        sessionId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        sessionToken = UUID.randomUUID();
        externalUserId = UUID.randomUUID().toString();
        gooruLms = Lms.gooru;
    }

    @Test
    public void generateTokenWhenProfileIsNull() throws Exception {
        SessionPostRequestDto sessionPostRequestDto = createSessionPostRequestDto();
        Client client = createClient();
        Session session = createSession();
        SessionTokenDto sessionTokenDto = createSessionTokenDto();
        Profile profile = createProfile();

        when(clientService.findByApiKeyAndApiSecret(apiKey, apiSecret)).thenReturn(client);
        doReturn(profile).when(sessionService, "createProfile", sessionPostRequestDto.getUser(), clientId);
        doThrow(new ContentNotFoundException("Message"))
                .when(sessionService, "findLastSessionProfileByClientIdAndExternalId", clientId, externalUserId);
        doReturn(session).when(sessionService, "createSession", profileId);
        doReturn(sessionTokenDto).when(sessionService, "getSessionToken", session);

        SessionTokenDto result = sessionService.generateToken(sessionPostRequestDto);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey), eq(apiSecret));
        verifyPrivate(sessionService, times(1))
                .invoke("findLastSessionProfileByClientIdAndExternalId", clientId, externalUserId);
        verifyPrivate(sessionService, times(0)).invoke("updateLastAccess", any());
        verifyPrivate(sessionService, times(1)).invoke("createProfile", sessionPostRequestDto.getUser(), clientId);
        verifyPrivate(sessionService, times(1)).invoke("createSession", profileId);
        verifyPrivate(sessionService, times(1)).invoke("getSessionToken", session);

        assertNotNull("Result is null", result);
        assertEquals("Wrong session token", sessionToken, result.getSessionToken());
    }

    @Test
    public void generateTokenWhenLastSessionIsNull() throws Exception {
        SessionPostRequestDto sessionPostRequestDto = createSessionPostRequestDto();
        Client client = createClient();
        Session session = createSession();
        SessionTokenDto sessionTokenDto = createSessionTokenDto();
        SessionProfileEntity sessionProfileEntity = createSessionProfile();

        when(clientService.findByApiKeyAndApiSecret(apiKey, apiSecret)).thenReturn(client);
        doReturn(sessionProfileEntity)
                .when(sessionService, "findLastSessionProfileByClientIdAndExternalId", clientId, externalUserId);
        doReturn(session).when(sessionService, "createSession", profileId);
        doReturn(sessionTokenDto).when(sessionService, "getSessionToken", session);

        SessionTokenDto result = sessionService.generateToken(sessionPostRequestDto);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey), eq(apiSecret));
        verifyPrivate(sessionService, times(1))
                .invoke("findLastSessionProfileByClientIdAndExternalId", clientId, externalUserId);
        verifyPrivate(sessionService, times(0)).invoke("updateLastAccess", any());
        verifyPrivate(sessionService, times(0)).invoke("createProfile", any(), any());
        verifyPrivate(sessionService, times(1)).invoke("createSession", profileId);
        verifyPrivate(sessionService, times(1)).invoke("getSessionToken", session);

        assertNotNull("Result is null", result);
        assertEquals("Wrong session token", sessionToken, result.getSessionToken());
    }

    @Test
    public void generateTokenWhenLastSessionIsNotNull() throws Exception {
        SessionPostRequestDto sessionPostRequestDto = createSessionPostRequestDto();
        Client client = createClient();
        Session session = createSession();
        SessionTokenDto sessionTokenDto = createSessionTokenDto();
        SessionProfileEntity sessionProfileEntity = createSessionProfile();

        when(sessionProfileEntity.getSessionId()).thenReturn(sessionId);

        when(clientService.findByApiKeyAndApiSecret(apiKey, apiSecret)).thenReturn(client);
        doReturn(sessionProfileEntity)
                .when(sessionService, "findLastSessionProfileByClientIdAndExternalId", clientId, externalUserId);
        doReturn(session).when(sessionService, "updateLastAccess", sessionId);
        doReturn(sessionTokenDto).when(sessionService, "getSessionToken", session);

        SessionTokenDto result = sessionService.generateToken(sessionPostRequestDto);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey), eq(apiSecret));
        verifyPrivate(sessionService, times(1))
                .invoke("findLastSessionProfileByClientIdAndExternalId", clientId, externalUserId);
        verifyPrivate(sessionService, times(1)).invoke("updateLastAccess", sessionId);
        verifyPrivate(sessionService, times(0)).invoke("createProfile", any(), any());
        verifyPrivate(sessionService, times(0)).invoke("createSession", profileId);
        verifyPrivate(sessionService, times(1)).invoke("getSessionToken", session);

        assertNotNull("Result is null", result);
        assertEquals("Wrong session token", sessionToken, result.getSessionToken());
    }

    @Test
    public void createProfilePrivateMethod() throws Exception {
        ExternalUserDto externalUser = createExternalUserDto();
        Profile profile = createProfile();

        when(profileService.saveProfileBasedOnExternalUser(externalUser, gooruLms, clientId)).thenReturn(profile);

        Profile result =
                WhiteboxImpl.invokeMethod(sessionService, "createProfile", externalUser, clientId);

        verify(profileService, times(1)).saveProfileBasedOnExternalUser(externalUser, gooruLms, clientId);
        assertEquals("Wrong profileId", profileId, result.getId());
        assertEquals("Wrong clientId", clientId, result.getClientId());
        assertEquals("Wrong externalId", externalUserId, result.getExternalId());
        assertEquals("Wrong lmsId", gooruLms, result.getLmsId());
    }

    @Test
    public void createSessionPrivateMethod() throws Exception {
        doReturn(createSession()).when(sessionService, "save", any(Session.class));

        Session result =
                WhiteboxImpl.invokeMethod(sessionService, "createSession", profileId);

        verifyPrivate(sessionService, times(1)).invoke("save", any(Session.class));
        assertEquals("Wrong profileId", profileId, result.getProfileId());
        assertEquals("Wrong sessionId", sessionId, result.getId());
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

    @Test(expected = ContentNotFoundException.class)
    public void findLastSessionProfileByClientIdAndExternalIdThrowsException() throws Exception {
        when(sessionRepository.findLastSessionProfileByClientIdAndExternalId(clientId, externalUserId))
                .thenReturn(null);
        WhiteboxImpl.invokeMethod(sessionService, "findLastSessionProfileByClientIdAndExternalId",
                clientId, externalUserId);
    }

    @Test
    public void findLastSessionProfileByClientIdAndExternalId() throws Exception {
        SessionProfileEntity sessionProfile = createSessionProfile();
        when(sessionRepository.findLastSessionProfileByClientIdAndExternalId(clientId, externalUserId))
                .thenReturn(sessionProfile);
        when(sessionProfile.getSessionId()).thenReturn(sessionId);

        SessionProfileEntity result =
                WhiteboxImpl.invokeMethod(sessionService, "findLastSessionProfileByClientIdAndExternalId",
                        clientId, externalUserId);

        verify(sessionRepository, times(1)).findLastSessionProfileByClientIdAndExternalId(clientId, externalUserId);
        assertEquals("Wrong session ID", sessionId, result.getSessionId());
        assertEquals("Wrong profile ID", profileId, result.getProfileId());
        assertEquals("Wrong client ID", clientId, result.getClientId());
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
        profile.setClientId(clientId);
        profile.setLmsId(gooruLms);
        profile.setExternalId(externalUserId);
        profile.setProfileData(gson.toJson(createExternalUserDto()));
        return profile;
    }

    private SessionTokenDto createSessionTokenDto() {
        SessionTokenDto sessionTokenDto = new SessionTokenDto();
        sessionTokenDto.setSessionToken(sessionToken);
        return sessionTokenDto;
    }

    private SessionProfileEntity createSessionProfile() {
        SessionProfileEntity sessionProfileEntity = mock(SessionProfileEntity.class);
        when(sessionProfileEntity.getClientId()).thenReturn(clientId);
        when(sessionProfileEntity.getProfileId()).thenReturn(profileId);
        return sessionProfileEntity;
    }
}