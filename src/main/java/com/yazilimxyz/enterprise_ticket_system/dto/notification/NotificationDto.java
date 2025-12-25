package com.yazilimxyz.enterprise_ticket_system.dto.notification;

import java.time.OffsetDateTime;

import com.yazilimxyz.enterprise_ticket_system.entities.enums.NotificationType;

public record NotificationDto(
                Long id,
                Long userId,
                String title,
                String message,
                NotificationType type,
                boolean isRead,
                OffsetDateTime createdAt) {
}
