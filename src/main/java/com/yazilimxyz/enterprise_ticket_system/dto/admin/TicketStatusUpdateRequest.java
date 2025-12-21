package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;

import lombok.Data;

@Data
public class TicketStatusUpdateRequest {
    private TicketStatus status;
}
