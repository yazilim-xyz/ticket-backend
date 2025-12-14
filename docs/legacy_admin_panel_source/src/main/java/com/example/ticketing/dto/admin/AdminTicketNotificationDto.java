package com.example.ticketing.dto.admin;

import com.example.ticketing.entity.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdminTicketNotificationDto {
    private Long id;
    private Long userId;
    private Long ticketId;
    private NotificationType type;
    private String message;
    private boolean read;
    private Instant createdAt;
}
