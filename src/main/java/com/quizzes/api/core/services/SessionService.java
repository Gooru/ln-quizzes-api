package com.quizzes.api.core.services;

import com.quizzes.api.core.dtos.ExternalUserDto;
import com.quizzes.api.core.dtos.SessionPostRequestDto;
import com.quizzes.api.core.dtos.SessionTokenDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidSessionException;
import com.quizzes.api.core.model.entities.SessionProfileEntity;
import com.quizzes.api.core.model.jooq.enums.Lms;
import com.quizzes.api.core.model.jooq.tables.pojos.Client;
import com.quizzes.api.core.model.jooq.tables.pojos.Profile;
import com.quizzes.api.core.model.jooq.tables.pojos.Session;
import com.quizzes.api.core.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    ClientService clientService;

    public SessionTokenDto generateToken(SessionPostRequestDto sessionData) {
        Client client =
                clientService.findByApiKeyAndApiSecret(sessionData.getClientApiKey(), sessionData.getClientApiSecret());
        ExternalUserDto externalUser = sessionData.getUser();
        SessionProfileEntity sessionProfile;
        UUID profileId;

        try {
            sessionProfile = findLastSessionProfileByClientIdAndExternalId(client.getId(), externalUser.getExternalId());
            if(sessionProfile.getSessionId() != null){
                return getSessionToken(updateLastAccess(sessionProfile.getSessionId()));
            }
            profileId = sessionProfile.getProfileId();
        } catch (ContentNotFoundException cne) {
            profileId = createProfile(externalUser, client.getId()).getId();
        }

        Session session = createSession(profileId);
        return getSessionToken(session);
    }

    private SessionProfileEntity findLastSessionProfileByClientIdAndExternalId(UUID clientId, String externalId) {
        SessionProfileEntity sessionProfile =
                sessionRepository.findLastSessionProfileByClientIdAndExternalId(clientId, externalId);
        if(sessionProfile == null){
            throw new ContentNotFoundException("There is no profile with for client ID: " + clientId +
            " and external ID: " + externalId);
        }
        return sessionProfile;
    }

    private Session createSession(UUID profileId) {
        Session session = new Session();
        session.setProfileId(profileId);
        return save(session);
    }

    private Profile createProfile(ExternalUserDto externalUser, UUID clientId) {
        //TODO: LmsId is being set with 'gooru' as default, we need to change that.
        return profileService.saveProfileBasedOnExternalUser(externalUser, Lms.gooru, clientId);
    }

    public Session findLastSessionByProfileId(UUID profileId) {
        Session session = sessionRepository.findLastSessionByProfileId(profileId);
        if (session == null) {
            throw new ContentNotFoundException("Active session not found for profile ID: " + profileId);
        }
        return session;
    }

    private SessionTokenDto getSessionToken(Session session) {
        SessionTokenDto token = new SessionTokenDto();
        token.setSessionToken(session.getId());
        return token;
    }

    public Profile findProfileBySessionId(UUID sessionId) {
        return sessionRepository.findProfileBySessionId(sessionId);
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    public Session updateLastAccess(UUID sessionId) {
        return sessionRepository.updateLastAccess(sessionId);
    }

    public SessionProfileEntity findSessionProfileEntityBySessionId(UUID sessionId) throws InvalidSessionException {
        SessionProfileEntity entity = sessionRepository.findSessionProfileEntityBySessionId(sessionId);
        if (entity == null || entity.getSessionId() == null) {
            throw new InvalidSessionException("Session ID: " + sessionId + " not found");
        }
        return entity;
    }

    public boolean isSessionAlive(UUID sessionId, Timestamp lastAccessAt, Timestamp currentTimestamp) {
        long diff = currentTimestamp.getTime() - lastAccessAt.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);

        return configurationService.getSessionMinutes() > diffInMinutes;
    }

}
