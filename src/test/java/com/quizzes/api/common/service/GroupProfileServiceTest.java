package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.repository.GroupProfileRepository;
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void getGroupProfilesByGroupId() throws Exception {
        List<GroupProfile> profiles = new ArrayList<>();
        UUID groupId = UUID.randomUUID();
        GroupProfile assignee1 = new GroupProfile(UUID.randomUUID(), groupId, UUID.randomUUID(), null);
        profiles.add(assignee1);
        GroupProfile assignee2 = new GroupProfile(UUID.randomUUID(), groupId, UUID.randomUUID(), null);
        profiles.add(assignee2);

        when(groupProfileRepository.getGroupProfilesByGroupId(groupId)).thenReturn(profiles);

        List<GroupProfile> result = groupProfileService.getGroupProfilesByGroupId(groupId);
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", 2, result.size());
        assertSame("Wrong data type", result.getClass(), ArrayList.class);
        assertSame("Wrong list contents data type", result.get(0).getClass(), GroupProfile.class);

    }

}