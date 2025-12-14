package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import lombok.Data;

@Data
public class ChangeUserStatusRequest {
    private String status; // ACTIVE / DISABLED
}
