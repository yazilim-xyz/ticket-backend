package com.yazilimxyz.enterprise_ticket_system.Repositories;

import com.yazilimxyz.enterprise_ticket_system.entities.TicketActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketActivityLogRepository extends JpaRepository<TicketActivityLog, Long> {

    Page<TicketActivityLog> findByUserId(Long userId, Pageable pageable);
}
