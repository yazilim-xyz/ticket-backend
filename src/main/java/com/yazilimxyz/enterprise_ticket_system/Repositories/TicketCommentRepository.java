package com.yazilimxyz.enterprise_ticket_system.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yazilimxyz.enterprise_ticket_system.entities.TicketComment;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    // Old method - N+1 problem!
    List<TicketComment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);

    // Optimized: Fetches comments with users in ONE query
    @Query("SELECT c FROM TicketComment c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.ticket.id = :ticketId " +
            "ORDER BY c.createdAt ASC")
    List<TicketComment> findByTicketIdWithUser(@Param("ticketId") Long ticketId);
}
