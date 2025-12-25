package com.yazilimxyz.enterprise_ticket_system.dto.ticket;

import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private TicketCategory category;
    private Long createdById;
    private String createdByName;
    private Long assignedToId;
    private String assignedToName;
    private OffsetDateTime dueDate;
    private String resolutionSummary;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
