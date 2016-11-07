package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.repository.GroupProfileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GroupProfileServiceTest {

    @InjectMocks
    private GroupProfileService groupProfileService = Mockito.spy(GroupProfileService.class);

    @Mock
    private GroupProfileRepository groupProfileRepository;

    @Test
    public void save() throws Exception {
        groupProfileService.save(any(GroupProfile.class));
        verify(groupProfileRepository, times(1)).save(any(GroupProfile.class));
    }
}