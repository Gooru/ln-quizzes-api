package com.quizzes.api.gooru.service;

import com.quizzes.api.common.dto.controller.ContextDTO;
import com.quizzes.api.common.model.Collection;
import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.common.service.ProfileServiceImpl;
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
        Context contextMock = new Context();
        contextMock.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        ContextDTO contextDTO = new ContextDTO();

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("courseId", "11111111-1111-1111-1111-111111111111");
        properties.put("classId", "22222222-2222-2222-2222-222222222222");
        properties.put("unitId", "33333333-3333-3333-3333-333333333333");
        properties.put("lessonId", "44444444-4444-4444-4444-44444444444");
        contextDTO.setContext(properties);

        Collection collection = new Collection();
        collection.setId(UUID.fromString("21111111-1111-1111-1111-111111111111"));

        when(collectionService.getOrCreateCollection("externalId")).thenReturn(collection);
        when(gooruContextRepository.save(any(Context.class))).thenReturn(contextMock);
        when(gooruContextRepository.findByCollectionIdAndContext(collection.getId(),
                contextDTO.getContext().get("courseId"),
                contextDTO.getContext().get("classId"),
                contextDTO.getContext().get("unitId"),
                contextDTO.getContext().get("lessonId"))).thenReturn(null);

        ResponseEntity<?> result = gooruService.getContext("externalId", contextDTO);
        verify(collectionService, times(1)).getOrCreateCollection(Mockito.eq("externalId"));
        verify(gooruContextRepository, times(1)).findByCollectionIdAndContext(Mockito.eq(collection.getId()),
                Mockito.eq(contextDTO.getContext().get("courseId")),
                Mockito.eq(contextDTO.getContext().get("classId")),
                Mockito.eq(contextDTO.getContext().get("unitId")),
                Mockito.eq(contextDTO.getContext().get("lessonId")));
        verify(gooruContextRepository, times(1)).save(any(Context.class));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(result.getStatusCode().value(), 201);
    }

    @Test
    public void getContextGet() throws Exception {
        Context contextMock = new Context();
        contextMock.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        ContextDTO contextDTO = new ContextDTO();

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("courseId", "11111111-1111-1111-1111-111111111111");
        properties.put("classId", "22222222-2222-2222-2222-222222222222");
        properties.put("unitId", "33333333-3333-3333-3333-333333333333");
        properties.put("lessonId", "44444444-4444-4444-4444-44444444444");
        contextDTO.setContext(properties);

        Collection collection = new Collection();
        collection.setId(UUID.fromString("21111111-1111-1111-1111-111111111111"));

        when(collectionService.getOrCreateCollection("externalId")).thenReturn(collection);
        when(gooruContextRepository.save(any(Context.class))).thenReturn(contextMock);
        when(gooruContextRepository.findByCollectionIdAndContext(collection.getId(),
                contextDTO.getContext().get("courseId"),
                contextDTO.getContext().get("classId"),
                contextDTO.getContext().get("unitId"),
                contextDTO.getContext().get("lessonId"))).thenReturn(contextMock);

        ResponseEntity<?> result = gooruService.getContext("externalId", contextDTO);
        verify(collectionService, times(1)).getOrCreateCollection(Mockito.eq("externalId"));
        verify(gooruContextRepository, times(1)).findByCollectionIdAndContext(Mockito.eq(collection.getId()),
                Mockito.eq(contextDTO.getContext().get("courseId")),
                Mockito.eq(contextDTO.getContext().get("classId")),
                Mockito.eq(contextDTO.getContext().get("unitId")),
                Mockito.eq(contextDTO.getContext().get("lessonId")));
        verify(gooruContextRepository, times(0)).save(any(Context.class));

        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(result.getStatusCode().value(), 200);
    }


}