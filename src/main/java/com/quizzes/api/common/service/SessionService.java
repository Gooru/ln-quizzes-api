package com.quizzes.api.common.service;

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
        Client client = clientService.findByApiKeyAndApiSecret(sessionData.getClientApiKey(), sessionData.getClientApiSecret());
        Session session = new Session();
        ExternalUserDto externalUser = sessionData.getUser();
        UUID profileId;

        try {
            profileId = profileService.findIdByExternalIdAndClientId(externalUser.getExternalId(), client.getId());
            try {
                Session lastSession = findLastSessionByProfileId(profileId);
                session = updateLastAccess(lastSession.getId());
                return getSessionToken(session);
            } catch(ContentNotFoundException cne){}
        } catch (ContentNotFoundException cne) {
            //TODO: LmsId is being set with 'gooru' as default, we need to change that.
            Profile profile = profileService.saveProfileBasedOnExternalUser(externalUser, Lms.gooru, client.getId());
            profileId = profile.getId();
        }

        session.setProfileId(profileId);
        return getSessionToken(save(session));
    }

    public Session findLastSessionByProfileId(UUID profileId){
        Session session = sessionRepository.findLastSessionByProfileId(profileId);
        if(session == null){
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
