package com.quizzes.api.common.interceptor;

import com.quizzes.api.common.exception.InvalidSessionException;
import com.quizzes.api.common.model.entities.SessionProfileEntity;
import com.quizzes.api.common.service.SessionService;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class SessionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");

        //TODO: temporal solution
        if (authorization != null) {
            String sessionToken = validateTokenFormat(authorization);
            SessionProfileEntity session = sessionService
                    .findSessionProfileEntityBySessionId(UUID.fromString(sessionToken));

            boolean isSessionAlive = sessionService
                    .isSessionAlive(session.getSessionId(), session.getLastAccessAt(), session.getCurrentTimestamp());

            if(!isSessionAlive){
                throw new InvalidSessionException("Session ID: " + session.getSessionId() + " expired");
            }

            request.setAttribute("profileId", session.getProfileId());
            request.setAttribute("clientId", session.getClientId());
            sessionService.updateLastAccess(session.getSessionId());
        }
        return true;
    }

    private String validateTokenFormat(String authorization) throws InvalidSessionException {
        if(authorization == null){
            throw new InvalidSessionException("Wrong token value, it must contain: Token <token>");
        }

        String[] sessionToken = authorization.split(" ");
        if (sessionToken.length != 2 || !sessionToken[0].equals("Token")) {
            throw new InvalidSessionException("Wrong token value, it must contain: Token <token>");
        }
        return sessionToken[1];
    }

}