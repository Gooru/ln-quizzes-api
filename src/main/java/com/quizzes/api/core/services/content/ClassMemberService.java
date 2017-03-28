package com.quizzes.api.core.services.content;

import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.rest.clients.ClassMemberRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClassMemberService {

    @Autowired
    private ClassMemberRestClient classMemberRestClient;

    public List<UUID> getClassMemberIds(UUID classId, String authToken) {
        return classMemberRestClient.getClassMembers(classId, authToken).getMemberIds();
    }

    public List<UUID> getClassOwnerIds(UUID classId, String authToken) {
        return classMemberRestClient.getClassMembers(classId, authToken).getOwnerIds();
    }

    public boolean containsMemberId(UUID classId, UUID memberId, String authToken) {
        return getClassMemberIds(classId, authToken).contains(memberId);
    }

    public boolean containsOwnerId(UUID classId, UUID ownerId, String authToken) {
        return getClassOwnerIds(classId, authToken).contains(ownerId);
    }

    public void validateClassMember(UUID classId, UUID profileId, String token) throws InvalidAssigneeException {
        if (classId != null) {
            if (!containsMemberId(classId, profileId, token)) {
                throw new InvalidAssigneeException("Profile Id: " + profileId + " is not a valid Assignee " +
                        "(member of the Class Id: " + classId + ")");
            }
        }
    }

}
