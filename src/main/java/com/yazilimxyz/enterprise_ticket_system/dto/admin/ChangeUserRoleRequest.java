package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import lombok.Data;

@Data
public class ChangeUserRoleRequest {
    private String role; // ADMIN / USER
}
