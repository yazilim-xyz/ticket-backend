package com.yazilimxyz.enterprise_ticket_system.controller.ticket;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketStatisticsdto;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketSimpledto;
import com.yazilimxyz.enterprise_ticket_system.dto.TicketDetaildto;

import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketAssignRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCommentCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCommentDto;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketCreateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketDto;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketResolutionStatsDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketResolutionUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.dto.ticket.TicketStatusUpdateRequest;
import com.yazilimxyz.enterprise_ticket_system.entities.enums.TicketStatus;
import com.yazilimxyz.enterprise_ticket_system.service.ticket.TicketService;
import jakarta.validation.Valid;

import java.time.OffsetDateTime;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // olusturma
    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@RequestBody TicketCreateRequest request) {
        TicketDto created = ticketService.createTicket(request);
        return ResponseEntity.ok(created);
    }

    // atama
    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketDto> assignTicket(@PathVariable Long id, @RequestBody TicketAssignRequest request) {
        TicketDto updated = ticketService.assignTicket(id, request);
        return ResponseEntity.ok(updated);
    }

    // durum degistirme
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketDto> updateStatus(@PathVariable Long id,
            @RequestBody TicketStatusUpdateRequest request) {
        TicketDto updated = ticketService.updateStatus(id, request);
        return ResponseEntity.ok(updated);
    }

    // comment kısmı
    @PostMapping("/{id}/comments")
    public ResponseEntity<TicketCommentDto> addComment(@PathVariable Long id,
            @RequestBody TicketCommentCreateRequest request) {
        TicketCommentDto comment = ticketService.addComment(id, request);
        return ResponseEntity.ok(comment);
    }

    // yorumları gösterme
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TicketCommentDto>> getComments(@PathVariable Long id) {
        List<TicketCommentDto> comments = ticketService.getComments(id);
        return ResponseEntity.ok(comments);

    }

    /**
     * Giriş yapan kullanıcıya atanan ticketları getir
     * GET /api/tickets/my-assigned
     * Optional params: status, startDate, endDate
     */
    @GetMapping("/my-assigned")
    public ResponseEntity<List<TicketSimpledto>> getMyAssignedTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) OffsetDateTime startDate,
            @RequestParam(required = false) OffsetDateTime endDate) {
        return ResponseEntity.ok(ticketService.getMyTickets(status, startDate, endDate));
    }

    /**
     * 1. Get ticket statistics for current user (last 30 days by default)
     * GET /api/tickets/statistics
     * Optional params: startDate, endDate (ISO format)
     */
    @GetMapping("/statistics")
    public ResponseEntity<TicketStatisticsdto> getMyTicketStatistics(
            @RequestParam(required = false) OffsetDateTime startDate,
            @RequestParam(required = false) OffsetDateTime endDate) {
        // Default to last 180 days if not provided
        if (startDate == null) {
            startDate = OffsetDateTime.now(ZoneOffset.UTC).minusDays(180);
        }
        if (endDate == null) {
            endDate = OffsetDateTime.now(ZoneOffset.UTC);
        }
        return ResponseEntity.ok(ticketService.getMyTicketStatistics(startDate, endDate));
    }

    /**
     * 4. Get ticket detail with all information
     * GET /api/tickets/{id}/detail
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<TicketDetaildto> getTicketDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketDetail(id));
    }

    /**
     * Top 5 en çok ticket çözen çalışanları getir
     * İstatistikler: çözülen sayı, çözülmeyen sayı, başarı yüzdesi, ortalama çözme
     * süresi
     */
    @GetMapping("/analytics/top-resolvers")
    public ResponseEntity<List<TicketResolutionStatsDTO>> getTopTicketResolvers() {
        List<TicketResolutionStatsDTO> stats = ticketService.getTopTicketResolvers();
        return ResponseEntity.ok(stats);
    }

    /**
     * Ticket'a çözüm özeti ekle/güncelle
     * PATCH /api/tickets/{id}/resolution
     */
    @PatchMapping("/{id}/resolution")
    public ResponseEntity<TicketDto> updateResolution(
            @PathVariable Long id,
            @Valid @RequestBody TicketResolutionUpdateRequest request) {
        TicketDto updated = ticketService.updateResolution(id, request.getResolutionSummary());
        return ResponseEntity.ok(updated);
    }

    // TODO ticket oluşturma falan adminin görevi sadece. buradan kaldırılması lazım
    // TODO ticket silme, user silme vb olaylar
}