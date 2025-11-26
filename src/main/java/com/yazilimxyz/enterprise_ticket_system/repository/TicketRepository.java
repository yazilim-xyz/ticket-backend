package com.yazilimxyz.enterprise_ticket_system.repository;

import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
