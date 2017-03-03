package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.ClassMemberContentDto;
import com.quizzes.api.core.rest.clients.ClassMemberRestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
public class ClassMemberServiceTest {

    @InjectMocks
    private ClassMemberService classMemberService = spy(new ClassMemberService());

    @Mock
    private ClassMemberRestClient classMemberRestClient;

    private UUID profileId;

    @Before
    public void before() throws Exception {
        profileId = UUID.randomUUID();
    }

    @Test
    public void getClassMemberIds() throws Exception {
        List<UUID> classMemberIds = new ArrayList<>();
        classMemberIds.add(UUID.randomUUID());
        ClassMemberContentDto classMemberContentDto = new ClassMemberContentDto();
        classMemberContentDto.setMemberIds(classMemberIds);

        doReturn(classMemberContentDto).when(classMemberRestClient).getClassMembers(any(UUID.class), anyString());

        List<UUID> result = classMemberService.getClassMemberIds(UUID.randomUUID(), "token");

        verify(classMemberRestClient, times(1)).getClassMembers(any(UUID.class), anyString());
        assertEquals("Wrong number of results", classMemberIds.size(), result.size());
    }

    @Test
    public void containsMemberIdIsTrue() throws Exception {
        List<UUID> classMemberIds = new ArrayList<>();
        classMemberIds.add(UUID.randomUUID());
        classMemberIds.add(profileId);
        ClassMemberContentDto classMemberContentDto = new ClassMemberContentDto();
        classMemberContentDto.setMemberIds(classMemberIds);

        doReturn(classMemberContentDto).when(classMemberRestClient).getClassMembers(any(UUID.class), anyString());

        boolean result = classMemberService.containsMemberId(UUID.randomUUID(), profileId, "token");

        verify(classMemberService, times(1)).getClassMemberIds(any(UUID.class), anyString());
        verify(classMemberRestClient, times(1)).getClassMembers(any(UUID.class), anyString());
        assertTrue("Wrong result value", result);
    }

    @Test
    public void containsMemberIdIsFalse() throws Exception {
        List<UUID> classMemberIds = new ArrayList<>();
        classMemberIds.add(UUID.randomUUID());
        ClassMemberContentDto classMemberContentDto = new ClassMemberContentDto();
        classMemberContentDto.setMemberIds(classMemberIds);

        doReturn(classMemberContentDto).when(classMemberRestClient).getClassMembers(any(UUID.class), anyString());

        boolean result = classMemberService.containsMemberId(UUID.randomUUID(), profileId, "token");

        verify(classMemberService, times(1)).getClassMemberIds(any(UUID.class), anyString());
        verify(classMemberRestClient, times(1)).getClassMembers(any(UUID.class), anyString());
        assertFalse("Wrong result value", result);
    }

}
