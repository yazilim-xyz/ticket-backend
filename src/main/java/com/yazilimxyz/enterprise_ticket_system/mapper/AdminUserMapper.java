package com.yazilimxyz.enterprise_ticket_system.mapper;

import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserResponseDto;
import com.yazilimxyz.enterprise_ticket_system.dto.admin.AdminUserUpdateRequest;
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

        dto.setName(user.getName());
        dto.setSurname(user.getSurname());

        dto.setRole(user.getRole() != null ? user.getRole().name() : null);

        dto.setActive(user.isActive());
        dto.setApproved(user.isApproved());
        dto.setDepartment(null);
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(null);

        return dto;
    }

    public void updateFromRequest(AdminUserUpdateRequest req, User user) {
        if (req == null || user == null) {
            return;
        }

        if (req.getName() != null || req.getSurname() != null) {
            user.setName(req.getName());
            user.setSurname(req.getSurname());
        }

        // department alanın yok – burayı şimdilik yorumda bırakıyoruz.
        // if (req.getDepartment() != null) {
        // user.setDepartment(req.getDepartment());
        // }
    }
}
