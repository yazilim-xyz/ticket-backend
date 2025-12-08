package com.example.ticketing.mapper;

import com.example.ticketing.dto.admin.AdminTicketNotificationDto;
import com.example.ticketing.entity.TicketNotification;

public final class AdminNotificationMapper {

    private AdminNotificationMapper() {
    }

    public static AdminTicketNotificationDto toDto(TicketNotification notification) {
        if (notification == null) {
            return null;
        }
        return AdminTicketNotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .ticketId(notification.getTicket() != null ? notification.getTicket().getId() : null)
                .type(notification.getType())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
