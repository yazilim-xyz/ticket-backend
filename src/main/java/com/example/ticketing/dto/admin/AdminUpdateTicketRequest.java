package com.example.ticketing.dto.admin;

import com.example.ticketing.entity.TicketPriority;
import com.example.ticketing.entity.TicketStatus;
import lombok.Data;

@Data
public class AdminUpdateTicketRequest {

    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private Long assignedToId;
}
