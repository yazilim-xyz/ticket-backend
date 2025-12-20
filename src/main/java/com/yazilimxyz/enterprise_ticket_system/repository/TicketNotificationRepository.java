package com.yazilimxyz.enterprise_ticket_system.repository;

import com.yazilimxyz.enterprise_ticket_system.entities.TicketNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketNotificationRepository extends JpaRepository<TicketNotification, Long> {

    List<TicketNotification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<TicketNotification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);
}
