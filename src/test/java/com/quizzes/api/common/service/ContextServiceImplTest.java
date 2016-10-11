package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.ContextDTO;
import com.quizzes.api.common.model.Collection;
import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.repository.ContextRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextServiceImplTest {

    @InjectMocks
    private ContextService contextService = Mockito.spy(ContextServiceImpl.class);

    @Mock
    CollectionService collectionService;

    @Mock
    ContextRepository contextRepository;

    @Test
    public void getContextGet() throws Exception {
        Context contextMock = new Context();
        contextMock.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        ContextDTO contextDTO = new ContextDTO();

        Collection collection = new Collection();
        collection.setId(UUID.fromString("21111111-1111-1111-1111-111111111111"));

        when(collectionService.getOrCreateCollection("externalId")).thenReturn(collection);
        when(contextRepository.save(any(Context.class))).thenReturn(contextMock);
        when(contextRepository.findByCollectionId(collection.getId())).thenReturn(contextMock);

        ResponseEntity<?> result = contextService.getContext("externalId", contextDTO);
        verify(collectionService, times(1)).getOrCreateCollection(Mockito.eq("externalId"));
        verify(contextRepository, times(1)).findByCollectionId(Mockito.eq(collection.getId()));
        verify(contextRepository, times(0)).save(any(Context.class));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(result.getStatusCode().value(), 200);
    }

    @Test
    public void getContextCreate() throws Exception {
        Context contextMock = new Context();
        contextMock.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        ContextDTO contextDTO = new ContextDTO();

        Collection collection = new Collection();
        collection.setId(UUID.fromString("21111111-1111-1111-1111-111111111111"));

        when(collectionService.getOrCreateCollection("externalId")).thenReturn(collection);
        when(contextRepository.save(any(Context.class))).thenReturn(contextMock);
        when(contextRepository.findByCollectionId(collection.getId())).thenReturn(null);

        ResponseEntity<?> result = contextService.getContext("externalId", contextDTO);
        verify(collectionService, times(1)).getOrCreateCollection(Mockito.eq("externalId"));
        verify(contextRepository, times(1)).findByCollectionId(Mockito.eq(collection.getId()));
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(result.getStatusCode().value(), 201);
    }

}