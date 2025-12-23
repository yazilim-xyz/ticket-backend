package com.yazilimxyz.enterprise_ticket_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yazilimxyz.enterprise_ticket_system.entities.Ticket;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    long countByAssignedToId(Long assignedToId);

    long countByAssignedToIdAndStatus(Long assignedToId, TicketStatus status);
    
    // Kullanıcıya atanan ticketları filtrelerle getir
    @Query("SELECT t FROM Ticket t WHERE t.assignedTo.id = :userId " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
           "AND t.isDeleted = false " +
           "ORDER BY t.createdAt DESC")
    List<Ticket> findMyTicketsFiltered(
     @Param("userId") Long userId,
      @Param("status") TicketStatus status,
      @Param("startDate") OffsetDateTime startDate,
      @Param("endDate") OffsetDateTime endDate);
    
    // Zaman aralığında oluşturulan ticketları getir
    @Query("SELECT t FROM Ticket t WHERE t.assignedTo.id = :userId " +
           "AND t.createdAt >= :startDate " +
           "AND t.createdAt <= :endDate " +
           "AND t.isDeleted = false")
    List<Ticket> findByAssignedToIdAndCreatedAtBetween(
    @Param("userId") Long userId,
   @Param("startDate") OffsetDateTime startDate,
   @Param("endDate") OffsetDateTime endDate);
   Ticket findTopByAssignedToIdOrderByCreatedAtDesc(Long userId);
   
   // Kullanıcıya atanan tüm ticketları getir
   List<Ticket> findByAssignedToId(Long userId);
   
   // Status'e göre filtrelenmiş ticketlar
   List<Ticket> findByAssignedToIdAndStatus(Long userId, TicketStatus status);
   
   // Status ve tarih aralığına göre filtrelenmiş ticketlar
   List<Ticket> findByAssignedToIdAndStatusAndCreatedAtBetween(
       Long userId, 
       TicketStatus status, 
       OffsetDateTime startDate, 
       OffsetDateTime endDate
   );

}
