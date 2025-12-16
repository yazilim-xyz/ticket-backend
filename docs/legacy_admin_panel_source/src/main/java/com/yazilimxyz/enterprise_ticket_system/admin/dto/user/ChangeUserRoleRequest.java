package com.yazilimxyz.enterprise_ticket_system.admin.dto.user;

import lombok.Data;

@Data
public class ChangeUserRoleRequest {
    private String role; // ADMIN / USER
}
