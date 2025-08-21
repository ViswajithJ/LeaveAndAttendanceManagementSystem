package com.godigit.LeaveAndAttendanceManagementSystem.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;

@Component
public class PermissionUtil {

    private final UserRepository userRepository;

    public PermissionUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Check if a user is part of a manager's team.
     */
    public boolean isTeamMember(Long managerId, Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getManager() != null &&
                        user.getManager().getId().equals(managerId))
                .orElse(false);
    }

    /**
     * Generalized access check for viewing another user’s data.
     */
    public void checkViewPermission(Long loggedInUserId, Role role, Long targetUserId) {
        if (role.equals(Role.EMPLOYEE)) {
            if (!loggedInUserId.equals(targetUserId)) {
                throw new AccessDeniedException("Employees can only view their own data.");
            }
        }

        if (role.equals(Role.MANAGER)) {
            if (!loggedInUserId.equals(targetUserId) &&
                    !isTeamMember(loggedInUserId, targetUserId)) {
                throw new AccessDeniedException("Managers can only view their own or team members' data.");
            }
        }

        // Admin → unrestricted
    }
}
