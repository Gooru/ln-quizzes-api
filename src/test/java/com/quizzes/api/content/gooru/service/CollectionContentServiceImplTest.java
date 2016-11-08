package com.quizzes.api.content.gooru.service;

import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.service.content.CollectionContentService;
import com.quizzes.api.content.gooru.dto.AssessmentDto;
import com.quizzes.api.content.gooru.rest.CollectionRestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionContentServiceImplTest {

    @InjectMocks
    private CollectionContentService collectionContentService = Mockito.spy(CollectionContentServiceImpl.class);

    @Mock
    CollectionRestClient collectionRestClient;

    @Test
    public void createCollectionCopy() throws Exception {

        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setId(UUID.randomUUID().toString());
        when(collectionRestClient.getAssessment(any(String.class))).thenReturn(assessmentDto);

        Profile owner = new Profile();
        owner.setId(UUID.randomUUID());

        String externalCollectionId = UUID.randomUUID().toString();
        Collection collection = collectionContentService.createCollectionCopy(externalCollectionId, owner);

        verify(collectionRestClient, times(1)).getAssessment(externalCollectionId);

        assertNotNull("Collection is null", collection);
    }

}
