package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.SessionPostRequestDto;
import com.quizzes.api.common.dto.SessionTokenDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.common.exception.InvalidCredentialsException;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.model.jooq.tables.pojos.Session;
import com.quizzes.api.common.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SessionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

            //TODO: We need to support profileService.findByExternalIdAndClientId
            UUID profileId = profileService
                    .findIdByExternalIdAndLmsId(externalUser.getExternalId(), Lms.gooru);

            if (profileId == null) {
                Profile profile = profileService
                        .saveProfileBasedOnExternalUser(externalUser, Lms.gooru, client.getId());
                profileId = profile.getId();
            } else {
                Session lastSession = sessionRepository.findLastSessionByProfileId(profileId);
                if (lastSession != null) {
                    session = sessionRepository.updateLastAccess(lastSession);
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

    private Profile findProfileBySessionId(UUID sessionId) {
        Profile profile = sessionRepository.findProfileBySessionId(sessionId);
        if (profile == null) {
            logger.error("We could not find a profile with ID: " + sessionId);
            throw new ContentNotFoundException("We could not find a profile with ID: " + sessionId);
        }
        return profile;
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }

}
