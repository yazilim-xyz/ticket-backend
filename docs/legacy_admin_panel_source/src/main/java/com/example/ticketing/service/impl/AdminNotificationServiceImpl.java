package com.example.ticketing.service.impl;

import com.example.ticketing.dto.admin.AdminTicketNotificationDto;
import com.example.ticketing.mapper.AdminNotificationMapper;
import com.example.ticketing.repository.TicketNotificationRepository;
import com.example.ticketing.service.admin.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final TicketNotificationRepository ticketNotificationRepository;

    @Override
    public List<AdminTicketNotificationDto> getNotifications(Long userId, Long ticketId) {
        if (userId != null && ticketId != null) {
            return ticketNotificationRepository.findByUserId(userId).stream()
                    .filter(n -> n.getTicket() != null && ticketId.equals(n.getTicket().getId()))
                    .map(AdminNotificationMapper::toDto)
                    .toList();
        } else if (userId != null) {
            return ticketNotificationRepository.findByUserId(userId)
                    .stream()
                    .map(AdminNotificationMapper::toDto)
                    .toList();
        } else if (ticketId != null) {
            return ticketNotificationRepository.findByTicketId(ticketId)
                    .stream()
                    .map(AdminNotificationMapper::toDto)
                    .toList();
        } else {
            return ticketNotificationRepository.findAll()
                    .stream()
                    .map(AdminNotificationMapper::toDto)
                    .toList();
        }
    }
}
