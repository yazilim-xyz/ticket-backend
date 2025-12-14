package com.yazilimxyz.enterprise_ticket_system.admin.controller;

import com.yazilimxyz.enterprise_ticket_system.admin.dto.ticket.*;
import com.yazilimxyz.enterprise_ticket_system.admin.service.AdminTicketService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketController {

    private final AdminTicketService service;

    @GetMapping
    public Page<AdminTicketResponseDto> getTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        TicketFilterRequest filter = new TicketFilterRequest();
        filter.setPage(page);
        filter.setSize(size);

        return service.getTickets(filter);
    }

    @GetMapping("/{id}")
    public AdminTicketResponseDto getTicket(@PathVariable Long id) {
        return service.getTicket(id);
    }

    @PatchMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id, @RequestBody TicketStatusUpdateRequest req) {
        service.updateTicketStatus(id, req);
    }

    @PatchMapping("/{id}/assign")
    public void assignTicket(@PathVariable Long id, @RequestBody TicketAssignRequest req) {
        service.assignTicket(id, req);
    }
}
