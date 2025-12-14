package com.example.ticketing.repository;

import com.example.ticketing.entity.TicketNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketNotificationRepository extends JpaRepository<TicketNotification, Long> {

    List<TicketNotification> findByUserId(Long userId);

    List<TicketNotification> findByTicketId(Long ticketId);
}
