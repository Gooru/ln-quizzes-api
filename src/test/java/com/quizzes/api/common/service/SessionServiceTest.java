package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.SessionPostRequestDto;
import com.quizzes.api.common.dto.SessionTokenDto;
import com.quizzes.api.common.exception.InvalidCredentialsException;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.model.jooq.tables.pojos.Session;
import com.quizzes.api.common.repository.SessionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SessionService.class)
public class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService = Mockito.spy(SessionService.class);

    @Mock
    SessionRepository sessionRepository;

    @Mock
    ClientService clientService;

    @Mock
    ProfileService profileService;

    @Test
    public void generateTokenWhenLastSessionIsNotNull() throws Exception {
        //Setting sessionDto
        UUID apiKey = UUID.randomUUID();
        UUID apiSecret = UUID.randomUUID();

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
        when(profileService.findIdByExternalIdAndLmsId(eq(userDto.getExternalId()), eq(lms))).thenReturn(profileId);

        //Setting session
        UUID sessionId = UUID.randomUUID();
        Session sessionResult = new Session();
        sessionResult.setProfileId(profileId);
        sessionResult.setId(sessionId);

        //Reusing same session as result since we don't have a way to validate the last_access_at
        when(sessionRepository.findLastSessionByProfileId(eq(profileId))).thenReturn(sessionResult);
        when(sessionRepository.updateLastAccess(any(Session.class))).thenReturn(sessionResult);

        SessionTokenDto result = sessionService.generateToken(session);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()));
        verify(profileService, times(1)).findIdByExternalIdAndLmsId(eq(userDto.getExternalId()), eq(lms));
        verify(profileService, times(0)).saveProfileBasedOnExternalUser(eq(userDto), eq(lms), eq(client.getId()));

        verify(sessionRepository, times(1)).findLastSessionByProfileId(eq(profileId));
        verify(sessionRepository, times(1)).updateLastAccess(any(Session.class));

        verify(sessionRepository, times(0)).save(any(Session.class));

        assertNotNull("Result is null", result);
        assertEquals("Wrong session Id", sessionId, result.getSessionToken());
    }

    @Test
    public void generateTokenWhenLastSessionIsNull() throws Exception {
        //Setting sessionDto
        UUID apiKey = UUID.randomUUID();
        UUID apiSecret = UUID.randomUUID();

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
        when(profileService.findIdByExternalIdAndLmsId(eq(userDto.getExternalId()), eq(lms))).thenReturn(profileId);

        //Last session is null
        when(sessionRepository.findLastSessionByProfileId(eq(profileId))).thenReturn(null);

        //Setting session
        UUID sessionId = UUID.randomUUID();
        Session sessionResult = new Session();
        sessionResult.setProfileId(profileId);
        sessionResult.setId(sessionId);

        when(sessionRepository.save(any(Session.class))).thenReturn(sessionResult);

        SessionTokenDto result = sessionService.generateToken(session);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()));
        verify(profileService, times(1)).findIdByExternalIdAndLmsId(eq(userDto.getExternalId()), eq(lms));
        verify(profileService, times(0)).saveProfileBasedOnExternalUser(eq(userDto), eq(lms), eq(client.getId()));

        verify(sessionRepository, times(1)).findLastSessionByProfileId(eq(profileId));
        verify(sessionRepository, times(0)).updateLastAccess(any());

        verify(sessionRepository, times(1)).save(any(Session.class));

        assertNotNull("Result is null", result);
        assertEquals("Wrong session Id", sessionId, result.getSessionToken());
    }

    @Test
    public void generateTokenWhenProfileIsNull() throws Exception {
        //Setting sessionDto
        UUID apiKey = UUID.randomUUID();
        UUID apiSecret = UUID.randomUUID();

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
        when(profileService.findIdByExternalIdAndLmsId(eq(userDto.getExternalId()), eq(lms))).thenReturn(null);

        //Setting Profile
        Profile profile = new Profile();
        UUID profileId = UUID.randomUUID();
        profile.setId(profileId);

        when(profileService
                .saveProfileBasedOnExternalUser(eq(userDto), eq(lms), eq(client.getId()))).thenReturn(profile);

        //Setting session
        UUID sessionId = UUID.randomUUID();
        Session sessionResult = new Session();
        sessionResult.setProfileId(profileId);
        sessionResult.setId(sessionId);

        when(sessionRepository.save(any(Session.class))).thenReturn(sessionResult);

        SessionTokenDto result = sessionService.generateToken(session);

        verify(clientService, times(1)).findByApiKeyAndApiSecret(eq(apiKey.toString()), eq(apiSecret.toString()));
        verify(profileService, times(1)).findIdByExternalIdAndLmsId(eq(userDto.getExternalId()), eq(lms));
        verify(profileService, times(1)).saveProfileBasedOnExternalUser(eq(userDto), eq(lms), eq(client.getId()));

        verify(sessionRepository, times(0)).findLastSessionByProfileId(any());
        verify(sessionRepository, times(0)).updateLastAccess(any());

        verify(sessionRepository, times(1)).save(any(Session.class));

        assertNotNull("Result is null", result);
        assertEquals("Wrong session Id", sessionId, result.getSessionToken());
    }

    @Test(expected = InvalidCredentialsException.class)
    public void generateTokenShouldThrowExceptionWhenClientIsNull() throws Exception {
        //Setting sessionDto
        UUID apiKey = UUID.randomUUID();
        UUID apiSecret = UUID.randomUUID();

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

}