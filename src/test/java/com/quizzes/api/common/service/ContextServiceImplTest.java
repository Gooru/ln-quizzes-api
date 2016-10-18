package com.quizzes.api.common.service;

import com.quizzes.api.common.model.Context;
import com.quizzes.api.common.repository.ContextRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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
        Context contextMock = new Context();
        assertNotNull(contextMock);
    }

}