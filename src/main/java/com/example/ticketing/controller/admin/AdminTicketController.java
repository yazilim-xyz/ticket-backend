package com.example.ticketing.controller.admin;

import com.example.ticketing.dto.admin.AdminAssignTicketRequest;
import com.example.ticketing.dto.admin.AdminCreateTicketCommentRequest;
import com.example.ticketing.dto.admin.AdminCreateTicketRequest;
import com.example.ticketing.dto.admin.AdminInternalChatMessageDto;
import com.example.ticketing.dto.admin.AdminTicketActivityLogDto;
import com.example.ticketing.dto.admin.AdminTicketCommentDto;
import com.example.ticketing.dto.admin.AdminTicketDto;
import com.example.ticketing.dto.admin.AdminUpdateTicketRequest;
import com.example.ticketing.dto.admin.AdminUpdateTicketStatusRequest;
import com.example.ticketing.entity.TicketPriority;
import com.example.ticketing.entity.TicketStatus;
import com.example.ticketing.service.admin.AdminTicketCommentService;
import com.example.ticketing.service.admin.AdminTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
public class AdminTicketController {

    private final AdminTicketService adminTicketService;
    private final AdminTicketCommentService adminTicketCommentService;

    @GetMapping
    public ResponseEntity<List<AdminTicketDto>> getAllTickets(@RequestParam(required = false) TicketStatus status,
                                                              @RequestParam(required = false) TicketPriority priority,
                                                              @RequestParam(required = false) Long createdById,
                                                              @RequestParam(required = false) Long assignedToId,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo) {
        return ResponseEntity.ok(adminTicketService.getAllTickets(status, priority, createdById, assignedToId, createdFrom, createdTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminTicketDto> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(adminTicketService.getTicketById(id));
    }

    @PostMapping
    public ResponseEntity<AdminTicketDto> createTicket(@Valid @RequestBody AdminCreateTicketRequest request) {
        AdminTicketDto created = adminTicketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminTicketDto> updateTicket(@PathVariable Long id,
                                                       @Valid @RequestBody AdminUpdateTicketRequest request) {
        return ResponseEntity.ok(adminTicketService.updateTicket(id, request));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<AdminTicketDto> assignTicket(@PathVariable Long id,
                                                       @Valid @RequestBody AdminAssignTicketRequest request) {
        return ResponseEntity.ok(adminTicketService.assignTicket(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminTicketDto> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody AdminUpdateTicketStatusRequest request) {
        return ResponseEntity.ok(adminTicketService.updateTicketStatus(id, request));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<AdminTicketCommentDto>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(adminTicketCommentService.getCommentsForTicket(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<AdminTicketCommentDto> addComment(@PathVariable Long id,
                                                            @Valid @RequestBody AdminCreateTicketCommentRequest request) {
        request.setTicketId(id);
        AdminTicketCommentDto created = adminTicketCommentService.addComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}/internal-chat")
    public ResponseEntity<List<AdminInternalChatMessageDto>> getInternalChat(@PathVariable Long id) {
        return ResponseEntity.ok(adminTicketService.getInternalChatMessages(id));
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<List<AdminTicketActivityLogDto>> getActivity(@PathVariable Long id) {
        return ResponseEntity.ok(adminTicketService.getActivityLogs(id));
    }
}
