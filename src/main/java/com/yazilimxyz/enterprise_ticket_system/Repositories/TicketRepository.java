package com.yazilimxyz.enterprise_ticket_system.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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

        long countByCreatedByIdAndStatus(Long createdById, TicketStatus status);

        // ==================== BASIC COUNTS ====================

        // Count total tickets assigned to user (all time)
        long countByAssignedToId(Long assignedToId);

        // Count tickets by status for a user (all time)
        long countByAssignedToIdAndStatus(Long assignedToId, TicketStatus status);

        // ==================== BASIC FINDS ====================

        // Find all tickets assigned to user
        List<Ticket> findByAssignedToId(Long assignedToId);

        // Find tickets by status
        List<Ticket> findByAssignedToIdAndStatus(Long assignedToId, TicketStatus status);

        // Find latest ticket by user (for latest ticket info)
        Optional<Ticket> findTopByAssignedToIdOrderByCreatedAtDesc(Long assignedToId);

        // ==================== DATE RANGE COUNTS ====================

        // Count tickets INSIDE date range (total)
        @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo.id = :userId " +
                        "AND t.isDeleted = false " +
                        "AND t.createdAt >= :startDate " +
                        "AND t.createdAt <= :endDate")
        long countByAssignedToIdAndCreatedAtBetween(@Param("userId") Long userId,
                        @Param("startDate") OffsetDateTime startDate,
                        @Param("endDate") OffsetDateTime endDate);

        // Count by status INSIDE date range
        @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo.id = :userId " +
                        "AND t.status = :status " +
                        "AND t.isDeleted = false " +
                        "AND t.createdAt >= :startDate " +
                        "AND t.createdAt <= :endDate")
        long countByAssignedToIdAndStatusAndDateRange(@Param("userId") Long userId,
                        @Param("status") TicketStatus status,
                        @Param("startDate") OffsetDateTime startDate,
                        @Param("endDate") OffsetDateTime endDate);

        // Find tickets INSIDE date range with status
        @Query("SELECT t FROM Ticket t WHERE t.assignedTo.id = :userId " +
                        "AND t.status = :status " +
                        "AND t.isDeleted = false " +
                        "AND t.createdAt >= :startDate " +
                        "AND t.createdAt <= :endDate " +
                        "ORDER BY t.createdAt DESC")
        List<Ticket> findByAssignedToIdAndStatusAndCreatedAtBetween(@Param("userId") Long userId,
                        @Param("status") TicketStatus status,
                        @Param("startDate") OffsetDateTime startDate,
                        @Param("endDate") OffsetDateTime endDate);

        // ==================== OVERDUE COUNTS ====================

        // Count tickets BEFORE start date (overdue - sadece geçmiş aylar, RESOLVED
        // hariç)
        @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo.id = :userId " +
                        "AND t.status != 'RESOLVED' " +
                        "AND t.isDeleted = false " +
                        "AND t.createdAt < :startDate")
        long countOverdueTickets(@Param("userId") Long userId,
                        @Param("startDate") OffsetDateTime startDate);

        // Count tickets by status BEFORE start date (overdue by status, RESOLVED dahil
        // edilebilir filtre ile)
        @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo.id = :userId " +
                        "AND t.status = :status " +
                        "AND t.isDeleted = false " +
                        "AND t.createdAt < :startDate")
        long countByStatusBeforeDate(@Param("userId") Long userId,
                        @Param("status") TicketStatus status,
                        @Param("startDate") OffsetDateTime startDate);

        // ==================== FILTERED LIST ====================

        // Find user's tickets with filters (for listing)
        @Query("SELECT t FROM Ticket t WHERE t.assignedTo.id = :userId " +
                        "AND t.isDeleted = false " +
                        "AND t.createdAt >= :startDate " +
                        "AND t.createdAt <= :endDate " +
                        "ORDER BY t.createdAt DESC")
        List<Ticket> findMyTicketsFiltered(@Param("userId") Long userId,
                        @Param("startDate") OffsetDateTime startDate,
                        @Param("endDate") OffsetDateTime endDate);

        // Find OVERDUE tickets (before start date, RESOLVED hariç)
        @Query("SELECT t FROM Ticket t WHERE t.assignedTo.id = :userId " +
                        "AND t.status != 'RESOLVED' " +
                        "AND t.isDeleted = false " +
                        "AND t.createdAt < :startDate " +
                        "ORDER BY t.createdAt DESC")
        List<Ticket> findOverdueTickets(@Param("userId") Long userId,
                        @Param("startDate") OffsetDateTime startDate);

}
