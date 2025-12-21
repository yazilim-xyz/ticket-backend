package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import lombok.Data;

@Data
public class AdminUserUpdateRequest {
    private String name;
    private String surname;
    private String department;
}
