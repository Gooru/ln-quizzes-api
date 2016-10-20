package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.repository.ContextRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ContextServiceImplTest {

    @InjectMocks
    private ContextService contextService = Mockito.spy(ContextServiceImpl.class);

    @Mock
    CollectionService collectionService;

    @Mock
    ContextRepository contextRepository;

    @Test
    public void createContext() throws Exception {
        AssignmentDTO assignmentDTO = new AssignmentDTO();
        CollectionDTO collectionDTO = new CollectionDTO();
        collectionDTO.setName("nametest");

        assignmentDTO.setCollection(collectionDTO);

        Context result = contextService.createContext(assignmentDTO);
        assertEquals(result.getId().getClass(), UUID.class);

        Context contextMock = new Context();
        assertNotNull(contextMock);
    }

}