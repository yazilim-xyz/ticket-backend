package com.example.ticketing.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdminTicketActivityLogDto {
    private Long id;
    private Long ticketId;
    private Long performedById;
    private String performedByName;
    private String action;
    private String oldValue;
    private String newValue;
    private Instant createdAt;
}
