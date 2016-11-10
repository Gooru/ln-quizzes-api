package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.repository.GroupRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupServiceTest {

    @InjectMocks
    private GroupService groupService = Mockito.spy(GroupService.class);

    @Mock
    private GroupRepository groupRepository;

    @Test
    public void createGroup() throws Exception {
        UUID owner = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Group group = new Group(id, owner, null, null);

        when(groupRepository.save(any(Group.class))).thenReturn(group);

        Group result = groupService.createGroup(owner);
        verify(groupService, times(1)).createGroup(Mockito.eq(owner));
        verify(groupRepository, times(1)).save(any(Group.class));
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", id, result.getId());
        assertEquals("Wrong id", owner, result.getOwnerProfileId());
        assertEquals("Wrong group data", group.getGroupData(), result.getGroupData());
    }

    @Test
    public void findById() throws Exception {
        UUID owner = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Group group = new Group(id, owner, null, null);

        when(groupRepository.findById(any(UUID.class))).thenReturn(group);

        Group result = groupService.findById(UUID.randomUUID());
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", id, result.getId());
        assertEquals("Wrong id", owner, result.getOwnerProfileId());

    }

}