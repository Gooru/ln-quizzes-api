package com.quizzes.api.gooru.service;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.model.Collection;
import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.gooru.repository.GooruContextRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GooruContextServiceImplTest {

    @InjectMocks
    private ContextService gooruService = Mockito.spy(GooruContextServiceImpl.class);

    @Mock
    CollectionService collectionService;

    @Mock
    GooruContextRepository gooruContextRepository;

    @Test
    public void getContextCreate() throws Exception {
//        Context contextMock = new Context();
//        contextMock.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
//        AssignmentDTO assignmentDTO = new AssignmentDTO();
//
//        Map<String, String> properties = new HashMap<String, String>();
//
//        Collection collection = new Collection();
//        collection.setId(UUID.fromString("21111111-1111-1111-1111-111111111111"));
//
//        when(collectionService.getOrCreateCollection("externalId")).thenReturn(collection);
//        when(gooruContextRepository.save(any(Context.class))).thenReturn(contextMock);
//
//        ResponseEntity<?> result = gooruService.getContext("externalId", assignmentDTO);
//        verify(collectionService, times(1)).getOrCreateCollection(Mockito.eq("externalId"));
//
//        assertNotNull(result);
//        assertNotNull(result.getBody());
//        assertEquals(result.getStatusCode().value(), 201);
    }

    @Test
    public void getContextGet() throws Exception {
//        Context contextMock = new Context();
//        contextMock.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
//        AssignmentDTO assignmentDTO = new AssignmentDTO();
//
//        Map<String, String> properties = new HashMap<String, String>();
//
//        Collection collection = new Collection();
//        collection.setId(UUID.fromString("21111111-1111-1111-1111-111111111111"));
//
//        when(collectionService.getOrCreateCollection("externalId")).thenReturn(collection);
//        when(gooruContextRepository.save(any(Context.class))).thenReturn(contextMock);
//
//        ResponseEntity<?> result = gooruService.getContext("externalId", assignmentDTO);
//        verify(collectionService, times(1)).getOrCreateCollection(Mockito.eq("externalId"));
//        verify(gooruContextRepository, times(0)).save(any(Context.class));
//
//        assertNotNull(result);
//        assertNotNull(result.getBody());
//        assertEquals(result.getStatusCode().value(), 200);
    }


}