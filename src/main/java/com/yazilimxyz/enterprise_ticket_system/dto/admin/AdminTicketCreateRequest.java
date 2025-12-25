package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AdminTicketCreateRequest {
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketCategory category;
    private Long assignedToUserId;
    private OffsetDateTime dueDate;
}
