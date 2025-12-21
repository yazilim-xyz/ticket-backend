package com.yazilimxyz.enterprise_ticket_system.service.admin;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.ChangeUserRoleRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.ChangeUserStatusRequest;

import org.springframework.data.domain.Page;

public interface AdminUserService {

    Page<AdminUserResponseDto> getUsers(int page, int size);

    AdminUserResponseDto getUser(Long id);

    AdminUserResponseDto createUser(AdminUserCreateRequest request);

    AdminUserResponseDto updateUser(Long id, AdminUserUpdateRequest request);

    void changeUserStatus(Long id, ChangeUserStatusRequest request);

    void changeUserRole(Long id, ChangeUserRoleRequest request);
}
