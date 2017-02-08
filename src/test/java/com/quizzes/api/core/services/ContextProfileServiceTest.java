package com.quizzes.api.core.services;

import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.repositories.ContextProfileRepository;
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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextProfileServiceTest {

    @InjectMocks
    private ContextProfileService contextProfileService = Mockito.spy(ContextProfileService.class);

    @Mock
    ContextProfileRepository contextProfileRepository;

    @Test
    public void findContextProfileByContextIdAndProfileId() throws Exception {
        when(contextProfileRepository.findByContextIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(new ContextProfile());
        ContextProfile context = contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextProfileRepository, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
    }

    @Test(expected = ContentNotFoundException.class)
    public void findContextProfileByContextIdAndProfileIdThrowException() throws Exception {
        when(contextProfileRepository.findByContextIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(null);
        ContextProfile context = contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
    }

    @Test
    public void findContextProfileIdsByContextId() throws Exception {
        List<UUID> ids = contextProfileService.findContextProfileIdsByContextId(any(UUID.class));
        verify(contextProfileRepository, times(1)).findContextProfileIdsByContextId(any(UUID.class));
    }

    @Test
    public void save() throws Exception {
        contextProfileService.save(any(ContextProfile.class));
        verify(contextProfileRepository, times(1)).save(any(ContextProfile.class));
    }

    @Test
    public void findContextProfileAttemptIds() throws Exception {
        List<UUID> contextProfileIds = new ArrayList<>();
        contextProfileIds.add(UUID.randomUUID());
        contextProfileIds.add(UUID.randomUUID());

        when(contextProfileRepository.findContextProfileIdsByContextIdAndProfileId(any(UUID.class), any(UUID.class))).
                thenReturn(contextProfileIds);

        List<UUID> result = contextProfileService.findContextProfileIdsByContextIdAndProfileId(any(UUID.class),
                any(UUID.class));

        verify(contextProfileRepository, times(1)).
                findContextProfileIdsByContextIdAndProfileId(any(UUID.class), any(UUID.class));
    }

}