package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import lombok.Data;

@Data
public class TicketFilterRequest {
    private String status;
    private String priority;
    private Long ownerId;
    private Long assignedToId;
    private int page = 0;
    private int size = 20;
}
