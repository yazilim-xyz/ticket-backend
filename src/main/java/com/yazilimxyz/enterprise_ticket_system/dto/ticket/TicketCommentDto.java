package com.yazilimxyz.enterprise_ticket_system.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCommentDto {
    private Long id;
    private Long ticketId;
    private Long userId;
    private String userName;
    private String commentText;
    private OffsetDateTime createdAt;
}
