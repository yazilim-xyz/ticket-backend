package com.yazilimxyz.enterprise_ticket_system.admin.service;

import com.yazilimxyz.enterprise_ticket_system.admin.dto.user.*;
import org.springframework.data.domain.Page;

public interface AdminUserService {

    Page<AdminUserResponseDto> getUsers(int page, int size);

    AdminUserResponseDto getUser(Long id);

    AdminUserResponseDto createUser(AdminUserCreateRequest request);

    AdminUserResponseDto updateUser(Long id, AdminUserUpdateRequest request);

    void changeUserStatus(Long id, ChangeUserStatusRequest request);

    void changeUserRole(Long id, ChangeUserRoleRequest request);
}
