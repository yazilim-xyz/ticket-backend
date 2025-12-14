package com.yazilimxyz.enterprise_ticket_system.admin.mapper;

import com.yazilimxyz.enterprise_ticket_system.admin.dto.user.AdminUserResponseDto;
import com.yazilimxyz.enterprise_ticket_system.admin.dto.user.AdminUserUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import org.springframework.stereotype.Component;

@Component
public class AdminUserMapper {

    public AdminUserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }

        AdminUserResponseDto dto = new AdminUserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());

        dto.setRole(user.getRole() != null ? user.getRole().name() : null);

        dto.setActive(user.isActive());
        dto.setDepartment(null);
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(null);

        return dto;
    }

    public void updateFromRequest(AdminUserUpdateRequest req, User user) {
        if (req == null || user == null) {
            return;
        }

        if (req.getFullName() != null) {
            user.setFullName(req.getFullName());
        }

        // department alanın yok – burayı şimdilik yorumda bırakıyoruz.
        // if (req.getDepartment() != null) {
        //     user.setDepartment(req.getDepartment());
        // }
    }
}
