package com.quizzes.api.common.service;

import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.repository.ClientRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClientService.class)
public class ClientServiceTest {

    @InjectMocks
    private ClientService clientService = Mockito.spy(ClientService.class);

    @Mock
    ClientRepository clientRepository;

    @Test
    public void findByApiKeyAndApiSecret() throws Exception {
        String apiKey = "key";
        String apiSecret = "secret";
        Client result = clientService.findByApiKeyAndApiSecret(apiKey, apiSecret);
        verify(clientRepository, times(1)).findByApiKeyAndApiSecret(eq(apiKey), eq(apiSecret));
    }

}