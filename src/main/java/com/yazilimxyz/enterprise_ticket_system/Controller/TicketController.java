package com.example.ticketapp.ticket;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // --- 1) Ticket Oluşturma ---
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketCreateRequest request) {
        Ticket created = ticketService.createTicket(request);
        return ResponseEntity.ok(created);
    }

    // --- 2) Ticket Atama ---
    @PatchMapping("/{id}/assign")
    public ResponseEntity<Ticket> assignTicket(@PathVariable Long id,
                                               @RequestBody TicketAssignRequest request) {
        Ticket updated = ticketService.assignTicket(id, request);
        return ResponseEntity.ok(updated);
    }

    // --- 3) Durum Güncelleme ---
    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateStatus(@PathVariable Long id,
                                               @RequestBody TicketStatusUpdateRequest request) {
        Ticket updated = ticketService.updateStatus(id, request);
        return ResponseEntity.ok(updated);
    }

    // --- 4) Yorum Ekleme ---
    @PostMapping("/{id}/comments")
    public ResponseEntity<TicketComment> addComment(@PathVariable Long id,
                                                    @RequestBody TicketCommentCreateRequest request) {
        TicketComment comment = ticketService.addComment(id, request);
        return ResponseEntity.ok(comment);
    }

    // --- 5) Ticket'ın yorumlarını listeleme ---
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TicketComment>> getComments(@PathVariable Long id) {
        List<TicketComment> comments = ticketService.getComments(id);
        return ResponseEntity.ok(comments);
    }
}
‚