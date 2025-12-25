package com.yazilimxyz.enterprise_ticket_system.service.admin;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.ChangeUserRoleRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.UserActivityDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.UserStatsDto;

import org.springframework.data.domain.Page;

public interface AdminUserService {

    Page<AdminUserResponseDto> getUsers(int page, int size, Boolean approved, Boolean active);

    AdminUserResponseDto getUser(Long id);

    AdminUserResponseDto createUser(AdminUserCreateRequest request);

    AdminUserResponseDto updateUser(Long id, AdminUserUpdateRequest request);

    void changeUserRole(Long id, ChangeUserRoleRequest request);

    void deleteUser(Long id);

    Page<AdminTicketResponseDto> getUserTickets(Long userId, int page, int size);

    UserStatsDto getUserStats(Long userId);

    Page<UserActivityDto> getUserActivity(Long userId, int page, int size);

    void updateUserActive(Long userId, Boolean active);

    void updateUserApproval(Long userId, Boolean approved);
}
