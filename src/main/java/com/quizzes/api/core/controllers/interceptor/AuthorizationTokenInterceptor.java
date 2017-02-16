package com.quizzes.api.core.controllers.interceptor;

import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@Component
public class AuthorizationTokenInterceptor extends HandlerInterceptorAdapter {

    private static final String PROFILE_ID_ATTRIBUTE = "profileId";
    private static final String CLIENT_ID_ATTRIBUTE = "clientId";
    private static final String TOKEN_ATTRIBUTE = "token";
    private static final String TOKEN_TYPE = "Token";

    @Autowired
    private AuthenticationRestClient authenticationRestClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = getToken(authorizationHeader);
        String[] decodedTokenValues = new String(Base64.getDecoder().decode(token)).split(":");

        //TODO Enable Token Authentication once Gooru team fixes the BE endpoint
        //authenticationRestClient.verifyAccessToken(token);

        request.setAttribute(PROFILE_ID_ATTRIBUTE, decodedTokenValues[2]);
        request.setAttribute(CLIENT_ID_ATTRIBUTE, decodedTokenValues[4]);
        request.setAttribute(TOKEN_ATTRIBUTE, token);
        return true;
    }

    private String getToken(String authorization) throws InvalidRequestException {
        if (authorization == null) {
            throw new InvalidRequestException("Authorization header is required");
        }

        String[] sessionToken = authorization.split("\\s");
        if (sessionToken.length != 2 || !sessionToken[0].equals(TOKEN_TYPE)) {
            throw new InvalidRequestException("Authorization header value is wrong, it must contain: Token <token>");
        }
        return sessionToken[1];
    }

}