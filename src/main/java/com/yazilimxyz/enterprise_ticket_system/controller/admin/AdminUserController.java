package com.yazilimxyz.enterprise_ticket_system.controller.admin;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.ChangeUserRoleRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.UpdateUserActiveRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.UpdateUserApprovalRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminTicketResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.UserActivityDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.UserStatsDto;
import com.yazilimxyz.enterprise_ticket_system.service.admin.AdminUserService;

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
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean approved,
            @RequestParam(required = false) Boolean active) {
        return service.getUsers(page, size, approved, active);
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

    @PatchMapping("/{id}/role")
    public void changeRole(@PathVariable Long id, @RequestBody ChangeUserRoleRequest req) {
        service.changeUserRole(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }

    @GetMapping("/{id}/tickets")
    public Page<AdminTicketResponseDto> getUserTickets(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.getUserTickets(id, page, size);
    }

    @GetMapping("/{id}/stats")
    public UserStatsDto getUserStats(@PathVariable Long id) {
        return service.getUserStats(id);
    }

    @GetMapping("/{id}/activity")
    public Page<UserActivityDto> getUserActivity(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.getUserActivity(id, page, size);
    }

    @PatchMapping("/{id}/active")
    public void updateUserActive(
            @PathVariable Long id,
            @RequestBody UpdateUserActiveRequest request) {
        service.updateUserActive(id, request.getActive());
    }

    @PatchMapping("/{id}/approval")
    public void updateUserApproval(
            @PathVariable Long id,
            @RequestBody UpdateUserApprovalRequest request) {
        service.updateUserApproval(id, request.getApproved());
    }
}
