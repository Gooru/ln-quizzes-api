package com.quizzes.api.common.interceptor;

import com.quizzes.api.common.model.entities.SessionProfileEntity;
import com.quizzes.api.common.service.SessionService;
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
        String sessionToken = request.getHeader("session-token");

        //TODO: temporal solution
        if (sessionToken != null) {
            SessionProfileEntity session = sessionService.findSessionProfileEntityBySessionId(UUID.fromString(sessionToken));
            sessionService.isSessionAlive(session.getSessionId(), session.getLastAccessAt(), session.getCurrentTimestamp());
            request.setAttribute("profileId", session.getProfileId());
            request.setAttribute("clientId", session.getClientId());
            sessionService.updateLastAccess(session.getSessionId());
        }
        return true;
    }

}