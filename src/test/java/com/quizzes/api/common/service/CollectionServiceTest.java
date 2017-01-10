package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.CollectionGetResponseDto;
import com.quizzes.api.common.dto.ResourceDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.jooq.enums.ContentProvider;
import com.quizzes.api.common.model.jooq.tables.pojos.Collection;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.CollectionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class CollectionServiceTest {

    @InjectMocks
    private CollectionService collectionService;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private ResourceService resourceService;

    @Mock
    Gson gson = new Gson();

    @Test
    public void findByExternalIdWhenExternalIdDoesNotExist() throws Exception {
        when(collectionRepository.findByExternalId(any(String.class))).thenReturn(null);
        Collection collection = collectionService.findByExternalId("external-id");
        assertNull("Collection should be null", collection);
    }

    @Test
    public void findByExternalIdWhenExternalIdExists() throws Exception {
        UUID collectionId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        String externalId = "external-id";
        String externalParentId = "external-parent-id";
        String collectionData = "{}";

        Collection collection = new Collection();
        collection.setId(collectionId);
        collection.setExternalId(externalId);
        collection.setExternalParentId(externalParentId);
        collection.setOwnerProfileId(profileId);
        collection.setIsCollection(true);
        collection.setContentProvider(ContentProvider.quizzes);
        collection.setCollectionData(collectionData);

        doReturn(collection).when(collectionRepository).findByExternalId(any(String.class));

        Collection result = collectionService.findByExternalId(externalId);
        assertEquals("Wrong collection id", collectionId, result.getId());
        assertEquals("Wrong owner profile", profileId, result.getOwnerProfileId());
        assertEquals("Wrong external id", externalId, result.getExternalId());
        assertEquals("Wrong external parent id", externalParentId, result.getExternalParentId());
        assertEquals("Wrong content provider", ContentProvider.quizzes, result.getContentProvider());

        assertEquals("Wrong collection data", collectionData, result.getCollectionData());
        assertTrue("Wrong isCollection value", result.getIsCollection());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findCollectionByIdWhenThrowsContentNotFoundException() throws Exception {
        when(collectionRepository.findById(any(UUID.class))).thenReturn(null);
        collectionService.findCollectionById(UUID.randomUUID());
    }

    @Test
    public void findCollectionByIdWhenIdExists() throws Exception {
        Collection collection = new Collection();
        UUID collectionId = UUID.randomUUID();
        collection.setId(collectionId);
        collection.setIsCollection(false);
        when(collectionRepository.findById(collectionId)).thenReturn(collection);

        List<Resource> resources = new ArrayList<>();
        Resource resource1 = new Resource();
        UUID resourceId = UUID.randomUUID();
        resource1.setId(resourceId);
        resource1.setIsResource(false);
        resource1.setSequence((short) 1);
        resource1.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"single_choice\"," +
                "\"correctAnswer\": [{\"value\": \"A\"}],\"body\": \"mocked body\",\"interaction\":" +
                " {\"shuffle\": true,\"maxChoices\": 10,\"prompt\": \"mocked Interaction\",\"choices\":" +
                " [{\"text\": \"option 1\",\"isFixed\": false,\"value\": \"A\"},{\"text\": \"option 2\"," +
                "\"isFixed\": false,\"value\": \"B\"},{\"text\": \"option 3\",\"isFixed\": false," +
                "\"value\": \"C\"}]}}");
        resources.add(resource1);

        Resource resource2 = new Resource();
        resource2.setId(UUID.randomUUID());
        resource2.setIsResource(false);
        resource2.setSequence((short) 1);
        resource2.setResourceData("{\"title\": \"mocked Question Data\",\"type\": \"true_false\",\"correctAnswer\":" +
                " [{\"value\": \"T\"}],\"body\": \"mocked body\",\"interaction\": {\"shuffle\": true,\"maxChoices\":" +
                " 10,\"prompt\": \"mocked Interaction\",\"choices\": [{\"text\": \"True\",\"isFixed\": false," +
                "\"value\": \"T\"},{\"text\": \"False\",\"isFixed\": false,\"value\": \"F\"}]}}");
        resources.add(resource2);
        when(resourceService.findByCollectionId(collection.getId())).thenReturn(resources);

        CollectionGetResponseDto result = collectionService.findCollectionById(collectionId);

        verify(collectionRepository, times(1)).findById(any(UUID.class));
        verify(resourceService, times(1)).findByCollectionId(collectionId);

        assertNotNull("Result is Null", result);
        assertEquals("Collection id is wrong", collectionId, result.getId());
        assertFalse("isCollection field is true", result.getIsCollection());
        assertEquals("Resource list size is wrong", 2, result.getResources().size());

        ResourceDto resultResource = result.getResources().get(0);
        assertFalse("IsResource field is true", resultResource.getIsResource());
        assertEquals("Wrong resource id", resourceId, resultResource.getId());
        assertEquals("Wrong sequence", 1, resultResource.getSequence());
        assertSame(result.getClass(), CollectionGetResponseDto.class);
    }

    @Test
    public void save() throws Exception {
        UUID collectionId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        String externalId = "external-id";
        String externalParentId = "external-parent-id";
        String collectionData = "{}";

        Collection collection = new Collection();
        collection.setId(collectionId);
        collection.setExternalId(externalId);
        collection.setExternalParentId(externalParentId);
        collection.setOwnerProfileId(profileId);
        collection.setIsCollection(true);
        collection.setContentProvider(ContentProvider.quizzes);
        collection.setCollectionData(collectionData);

        doReturn(collection).when(collectionRepository).save(collection);

        Collection result = collectionService.save(collection);
        assertEquals("Wrong collection id", collectionId, result.getId());
        assertEquals("Wrong owner profile", profileId, result.getOwnerProfileId());
        assertEquals("Wrong external id", externalId, result.getExternalId());
        assertEquals("Wrong external parent id", externalParentId, result.getExternalParentId());
        assertEquals("Wrong content provider", ContentProvider.quizzes, result.getContentProvider());

        assertEquals("Wrong collection data", collectionData, result.getCollectionData());
        assertTrue("Wrong isCollection value", result.getIsCollection());
    }

}