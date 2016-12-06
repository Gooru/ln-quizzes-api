package com.quizzes.api.common.service;

import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.common.repository.ContextProfileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ContextProfileServiceTest {

    @InjectMocks
    private ContextProfileService contextProfileService = Mockito.spy(ContextProfileService.class);

    @Mock
    ContextProfileRepository contextProfileRepository;

    @Test
    public void findContextProfileByContextIdAndProfileId() throws Exception {
        ContextProfile context = contextProfileRepository.findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextProfileRepository, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
    }

    @Test
    public void findContextProfileIdsByContextId() throws Exception {
        List<UUID> ids = contextProfileRepository.findContextProfileIdsByContextId(any(UUID.class));
        verify(contextProfileRepository, times(1)).findContextProfileIdsByContextId(any(UUID.class));
    }

    @Test
    public void delete() throws Exception {
        contextProfileService.delete(any(UUID.class));
        verify(contextProfileRepository, times(1)).delete(any(UUID.class));
    }

    @Test
    public void save() throws Exception {
        contextProfileService.save(any(ContextProfile.class));
        verify(contextProfileRepository, times(1)).save(any(ContextProfile.class));
    }

}