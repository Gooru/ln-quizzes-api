package com.quizzes.api.common.service;

import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.ResourceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ResourceServiceTest {

    @InjectMocks
    private ResourceService resourceService = Mockito.spy(ResourceService.class);

    @Mock
    private ResourceRepository resourceRepository;

    @Test
    public void findByCollectionId() {
        List<Resource> resources = new ArrayList<>();
        Resource resource1 = new Resource();
        resource1.setId(UUID.randomUUID());
        resource1.setIsResource(true);
        resource1.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"SingleChoice\"," +
                "\"correctAnswer\": [{\"value\": \"A\"}],\"body\": \"mocked body\",\"interaction\":" +
                " {\"shuffle\": true,\"maxChoices\": 10,\"prompt\": \"mocked Interaction\",\"choices\":" +
                " [{\"text\": \"option 1\",\"isFixed\": false,\"value\": \"A\"},{\"text\": \"option 2\",\"isFixed\":" +
                " false,\"value\": \"B\"},{\"text\": \"option 3\",\"isFixed\": false,\"value\": \"C\"}]}}");
        resources.add(resource1);

        Resource resource2 = new Resource();
        resource2.setId(UUID.randomUUID());
        resource2.setIsResource(true);
        resource2.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"True/False\",\"correctAnswer\":" +
                " [{\"value\": \"T\"}],\"body\": \"mocked body\",\"interaction\": {\"shuffle\": true,\"maxChoices\":" +
                " 10,\"prompt\": \"mocked Interaction\",\"choices\": [{\"text\": \"True\",\"isFixed\": false,\"value\": " +
                "\"T\"},{\"text\": \"False\",\"isFixed\": false,\"value\": \"F\"}]}}");
        resources.add(resource2);
        when(resourceRepository.findByCollectionId(any(UUID.class))).thenReturn(resources);

        List<Resource> result = resourceService.findByCollectionId(UUID.randomUUID());
        verify(resourceRepository, times(1)).findByCollectionId(any(UUID.class));

        assertNotNull("Result is null", result);
        assertEquals("Resources size doesn't match", 2, result.size());
        assertSame("Result is not a List", ArrayList.class, result.getClass());
        assertSame("Result contents are not Resources", Resource.class, result.get(0).getClass());
    }

    @Test
    public void findFirstByOrderBySequenceAscByContextId() {
        Resource result = resourceService.findFirstBySequenceByContextId(UUID.randomUUID());
        verify(resourceRepository, times(1)).findFirstBySequenceByContextId(any(UUID.class));
    }

    @Test
    public void save() {
        Resource result = resourceService.save(new Resource());
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    public void findById() {
        Resource result = resourceService.findById(UUID.randomUUID());
        verify(resourceRepository, times(1)).findById(any(UUID.class));
    }



}
