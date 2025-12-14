package com.yazilimxyz.enterprise_ticket_system.admin.dto.user;

import lombok.Data;

@Data
public class ChangeUserStatusRequest {
    private String status; // ACTIVE / DISABLED
}
