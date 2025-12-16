package com.example.ticketing.repository;

import com.example.ticketing.entity.TicketActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketActivityLogRepository extends JpaRepository<TicketActivityLog, Long> {

    List<TicketActivityLog> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
}
