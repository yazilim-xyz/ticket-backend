package com.yazilimxyz.enterprise_ticket_system.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}