package com.yazilimxyz.enterprise_ticket_system.dto;

import com.yazilimxyz.enterprise_ticket_system.entities.TicketComment;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketCategory;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketPriority;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetaildto {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private TicketCategory category;
    private String createdByName;
    private Long createdById;
    private String assignedToName;
    private Long assignedToId;
    private OffsetDateTime dueDate;
    private String resolutionSummary;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<TicketComment> comments;
}