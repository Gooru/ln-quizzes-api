package com.quizzes.api.common.service;

import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.common.repository.CurrentContextProfileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class CurrentContextProfileServiceTest {

    @InjectMocks
    private CurrentContextProfileService currentContextProfileService;

    @Mock
    CurrentContextProfileRepository currentContextProfileRepository;

    private UUID contextId;
    private UUID contextProfileId;
    private UUID profileId;
    private CurrentContextProfile currentContextProfile;

    @Before
    public void beforeEachTest() {
        contextId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        currentContextProfile = new CurrentContextProfile();
    }

    @Test
    public void findByContextIdAndProfileId() throws Exception {
        currentContextProfile = createCurrentContextProfile();
        when(currentContextProfileRepository.findByContextIdAndProfileId(eq(contextId), eq(profileId)))
                .thenReturn(currentContextProfile);

        CurrentContextProfile result = currentContextProfileService.findByContextIdAndProfileId(contextId, profileId);
        verify(currentContextProfileRepository, times(1)).findByContextIdAndProfileId(eq(contextId), eq(profileId));

        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong context profile ID", contextProfileId, result.getContextProfileId());
        assertEquals("Wrong profile ID", profileId, result.getProfileId());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findByContextIdAndProfileIdThrowException() throws Exception {
        when(currentContextProfileRepository.findByContextIdAndProfileId(eq(contextId), eq(profileId)))
                .thenReturn(null);
        CurrentContextProfile result = currentContextProfileService.findByContextIdAndProfileId(contextId, profileId);
    }

    @Test
    public void create() throws Exception {
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        currentContextProfileService.create(currentContextProfile);
        verify(currentContextProfileRepository, times(1)).create(eq(currentContextProfile));
    }

    @Test
    public void delete() throws Exception {
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        currentContextProfileService.delete(currentContextProfile);
        verify(currentContextProfileRepository, times(1)).delete(eq(currentContextProfile));
    }

    private CurrentContextProfile createCurrentContextProfile(){
        currentContextProfile.setContextProfileId(contextProfileId);
        currentContextProfile.setContextId(contextId);
        currentContextProfile.setProfileId(profileId);
        return currentContextProfile;
    }

}