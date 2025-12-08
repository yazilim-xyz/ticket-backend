package com.example.ticketing.service.admin;

import com.example.ticketing.dto.admin.AdminCreateUserRequest;
import com.example.ticketing.dto.admin.AdminUpdateUserRequest;
import com.example.ticketing.dto.admin.AdminUserDto;

import java.util.List;

public interface AdminUserService {
    List<AdminUserDto> getAllUsers();

    AdminUserDto getUserById(Long id);

    AdminUserDto createUser(AdminCreateUserRequest request);

    AdminUserDto updateUser(Long id, AdminUpdateUserRequest request);

    void enableUser(Long id, boolean enabled);

    void deleteUser(Long id);
}
