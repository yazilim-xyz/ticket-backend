package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import lombok.Data;

@Data
public class AdminUserCreateRequest {
    private String email;
    private String fullName;
    private String password;
    private String role;
    private String department;
}
