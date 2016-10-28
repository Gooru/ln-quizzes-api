package com.quizzes.api.common.service;

import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GroupServiceImplTest {

    @InjectMocks
    private GroupService groupService = Mockito.spy(GroupServiceImpl.class);

    @Test
    public void createGroup() throws Exception {
        UUID owner = UUID.randomUUID();

        Group result = groupService.createGroup(owner);
        verify(groupService, times(1)).createGroup(Mockito.eq(owner));
        assertNull("Response is not null", result);
    }

}