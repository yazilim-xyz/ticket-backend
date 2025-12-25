package com.yazilimxyz.enterprise_ticket_system.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByCreatedById(Long createdById, Pageable pageable);

    Page<Ticket> findByAssignedToId(Long assignedToId, Pageable pageable);

    Page<Ticket> findByCreatedByIdOrAssignedToId(Long createdById, Long assignedToId, Pageable pageable);

    long countByCreatedById(Long createdById);

    long countByAssignedToId(Long assignedToId);

    long countByCreatedByIdAndStatus(Long createdById, TicketStatus status);

    long countByAssignedToIdAndStatus(Long assignedToId, TicketStatus status);
}
