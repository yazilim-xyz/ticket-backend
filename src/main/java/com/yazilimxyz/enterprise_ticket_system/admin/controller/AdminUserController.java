package com.yazilimxyz.enterprise_ticket_system.admin.controller;

import com.yazilimxyz.enterprise_ticket_system.admin.dto.user.*;
import com.yazilimxyz.enterprise_ticket_system.admin.service.AdminUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService service;

    @GetMapping
    public Page<AdminUserResponseDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.getUsers(page, size);
    }

    @GetMapping("/{id}")
    public AdminUserResponseDto getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    @PostMapping
    public AdminUserResponseDto createUser(@RequestBody AdminUserCreateRequest req) {
        return service.createUser(req);
    }

    @PutMapping("/{id}")
    public AdminUserResponseDto updateUser(
            @PathVariable Long id,
            @RequestBody AdminUserUpdateRequest req) {
        return service.updateUser(id, req);
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(@PathVariable Long id, @RequestBody ChangeUserStatusRequest req) {
        service.changeUserStatus(id, req);
    }

    @PatchMapping("/{id}/role")
    public void changeRole(@PathVariable Long id, @RequestBody ChangeUserRoleRequest req) {
        service.changeUserRole(id, req);
    }
}
