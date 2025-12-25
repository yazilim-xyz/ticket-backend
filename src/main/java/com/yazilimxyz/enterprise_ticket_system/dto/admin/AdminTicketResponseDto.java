package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import lombok.*;
import java.time.OffsetDateTime;

import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTicketResponseDto {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private Long ownerId;
    private String ownerEmail;
    private Long assignedToId;
    private String assignedToEmail;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
