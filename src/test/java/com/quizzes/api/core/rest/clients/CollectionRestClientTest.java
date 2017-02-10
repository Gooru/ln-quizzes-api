package com.quizzes.api.core.rest.clients;

import com.quizzes.api.core.dtos.content.CollectionContentDto;
import com.quizzes.api.core.dtos.content.ResourceContentDto;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CollectionRestClient.class})
public class CollectionRestClientTest {

    @InjectMocks
    private CollectionRestClient collectionRestClient = spy(new CollectionRestClient());

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AuthenticationRestClient authenticationRestClient;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private GooruHelper gooruHelper;

    private String userToken;
    private UUID collectionId;
    private String url;

    @Before
    public void before() throws Exception {
        userToken = "user-token";
        collectionId = UUID.randomUUID();
        url = "http://www.gooru.org";
    }

    @Test
    public void getCollection() throws Exception {
        CollectionContentDto collectionContentDto = new CollectionContentDto();
        collectionContentDto.setId(collectionId.toString());

        doReturn(new ResponseEntity<>(collectionContentDto, HttpStatus.OK)).when(restTemplate)
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(CollectionContentDto.class));
        doReturn(new HttpHeaders()).when(gooruHelper).setupHttpHeaders(userToken);
        doReturn(url).when(configurationService).getContentApiUrl();

        CollectionContentDto result = collectionRestClient.getCollection(collectionId, userToken);

        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(CollectionContentDto.class));
        verify(gooruHelper, times(1)).setupHttpHeaders(userToken);
        verify(configurationService, times(1)).getContentApiUrl();
        assertTrue("Collection is false", result.getCollection());
    }

}
