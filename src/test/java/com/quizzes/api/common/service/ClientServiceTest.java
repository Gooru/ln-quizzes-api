package com.quizzes.api.common.service;

import com.quizzes.api.common.exception.InvalidCredentialsException;
import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.repository.ClientRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    private String apiKey;
    private String apiSecret;

    @Before
    public void beforeEachTest() {
        apiKey = "key";
        apiSecret = "secret";
    }

    @Test
    public void findByApiKeyAndApiSecret() throws Exception {
        when(clientRepository.findByApiKeyAndApiSecret(apiKey, apiSecret)).thenReturn(new Client());
        Client result = clientService.findByApiKeyAndApiSecret(apiKey, apiSecret);
        verify(clientRepository, times(1)).findByApiKeyAndApiSecret(eq(apiKey), eq(apiSecret));
        assertNotNull("Client is null", result);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void findByApiKeyAndApiSecretThrowsException() throws Exception {
        when(clientRepository.findByApiKeyAndApiSecret(apiKey, apiSecret)).thenReturn(null);
        Client result = clientService.findByApiKeyAndApiSecret(apiKey, apiSecret);
        verify(clientRepository, times(1)).findByApiKeyAndApiSecret(eq(apiKey), eq(apiSecret));
    }

}