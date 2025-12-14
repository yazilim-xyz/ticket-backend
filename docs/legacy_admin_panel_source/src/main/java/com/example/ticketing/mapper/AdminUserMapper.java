package com.example.ticketing.mapper;

import com.example.ticketing.dto.admin.AdminCreateUserRequest;
import com.example.ticketing.dto.admin.AdminUpdateUserRequest;
import com.example.ticketing.dto.admin.AdminUserDto;
import com.example.ticketing.entity.User;

import java.util.HashSet;
import java.util.Set;

public final class AdminUserMapper {

    private AdminUserMapper() {
    }

    public static AdminUserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return AdminUserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .roles(new HashSet<>(user.getRoles()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static User fromCreateRequest(AdminCreateUserRequest request) {
        Set<String> roles = request.getRoles() == null || request.getRoles().isEmpty()
                ? Set.of("ROLE_USER")
                : new HashSet<>(request.getRoles());

        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles(roles)
                .enabled(true)
                .build();
    }

    public static void updateEntity(User user, AdminUpdateUserRequest request) {
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(new HashSet<>(request.getRoles()));
        }
    }
}
