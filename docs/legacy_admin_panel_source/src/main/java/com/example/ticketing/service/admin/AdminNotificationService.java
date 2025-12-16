package com.example.ticketing.service.admin;

import com.example.ticketing.dto.admin.AdminTicketNotificationDto;

import java.util.List;

public interface AdminNotificationService {

    List<AdminTicketNotificationDto> getNotifications(Long userId, Long ticketId);
}
