package com.yazilimxyz.enterprise_ticket_system.dto;

import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketSimpledto {
    private Long id;
    private String title;
    private TicketStatus status;
    private TicketPriority priority;
    private TicketCategory category;
    private String createdByName;
    private String assignedToName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
   @NotNull private OffsetDateTime dueDate;
}