package com.quizzes.api.core.services;

import com.google.code.ssm.api.InvalidateSingleCache;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;
import com.google.code.ssm.api.ReturnDataUpdateContent;
import com.google.code.ssm.api.UpdateSingleCache;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TokenService {

    public static final int TOKEN_EXPIRATION_TIME_IN_SECONDS = 900; // 15 minutes
    private AuthenticationRestClient authenticationRestClient;

    @Autowired
    public TokenService(AuthenticationRestClient authenticationRestClient) {
        Assert.notNull(authenticationRestClient, "Authentication REST client cannot be null");

        this.authenticationRestClient = authenticationRestClient;
    }

    @ReadThroughSingleCache(namespace = "Tokens", expiration = TOKEN_EXPIRATION_TIME_IN_SECONDS)
    public boolean validate(@ParameterValueKeyProvider String token) {
        update(token);
        return true;
    }

    @ReturnDataUpdateContent
    @UpdateSingleCache(namespace = "Tokens", expiration = TOKEN_EXPIRATION_TIME_IN_SECONDS)
    public boolean update(@ParameterValueKeyProvider String token) {
        authenticationRestClient.verifyAccessToken(token);
        return true;
    }

    @InvalidateSingleCache(namespace = "Tokens")
    public void delete(@ParameterValueKeyProvider String token) {

    }
}
