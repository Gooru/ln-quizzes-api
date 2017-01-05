package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.SessionPostRequestDto;
import com.quizzes.api.common.dto.SessionTokenDto;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.common.exception.InvalidCredentialsException;
import com.quizzes.api.common.exception.InvalidSessionException;
import com.quizzes.api.common.model.entities.SessionProfileEntity;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.model.jooq.tables.pojos.Session;
import com.quizzes.api.common.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        if (client == null) {
            logger.error("Invalid credentials for " + sessionData.getClientApiKey());
            throw new InvalidCredentialsException("Invalid client credentials.");
        }
        try {
            Session session = new Session();
            ExternalUserDto externalUser = sessionData.getUser();

            UUID profileId = profileService
                    .findIdByExternalIdAndClientId(externalUser.getExternalId(), client.getId());

            if (profileId == null) {
                //TODO: LmsId is being set with 'gooru' as default, we need to change that.
                Profile profile = profileService
                        .saveProfileBasedOnExternalUser(externalUser, Lms.gooru, client.getId());
                profileId = profile.getId();
            } else {
                Session lastSession = sessionRepository.findLastSessionByProfileId(profileId);
                if (lastSession != null) {
                    session = updateLastAccess(lastSession.getId());
                    return getSessionToken(session);
                }
            }

            session.setProfileId(profileId);
            return getSessionToken(save(session));
        } catch (Exception e) {
            logger.error("We could not generate a token.", e);
            throw new InternalServerException("We could not generate a token.", e);
        }
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

    public boolean isSessionAlive(UUID sessionId, Timestamp lastAccessAt, Timestamp currentTimestamp)
            throws InvalidSessionException {
        long diff = currentTimestamp.getTime() - lastAccessAt.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);

        if (diffInMinutes > configurationService.getSessionMinutes()) {
            throw new InvalidSessionException("Session ID: " + sessionId + " expired");
        }
        return true;
    }

}
