package com.example.ticketing.dto.admin;

import com.example.ticketing.entity.TicketPriority;
import com.example.ticketing.entity.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdminTicketDto {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private Long createdById;
    private String createdByName;
    private Long assignedToId;
    private String assignedToName;
    private Instant createdAt;
    private Instant updatedAt;
}
