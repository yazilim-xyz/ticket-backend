package com.example.ticketing.controller.admin;

import com.example.ticketing.dto.admin.AdminAiTicketSuggestionDto;
import com.example.ticketing.dto.admin.AdminCreateTicketFromSuggestionRequest;
import com.example.ticketing.dto.admin.AdminTicketDto;
import com.example.ticketing.dto.admin.AdminUpdateAiSuggestionStatusRequest;
import com.example.ticketing.service.admin.AdminAiSuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ai-suggestions")
@RequiredArgsConstructor
public class AdminAiSuggestionController {

    private final AdminAiSuggestionService adminAiSuggestionService;

    @GetMapping
    public ResponseEntity<List<AdminAiTicketSuggestionDto>> getAll() {
        return ResponseEntity.ok(adminAiSuggestionService.getAll());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminAiTicketSuggestionDto> updateStatus(@PathVariable Long id,
                                                                   @Valid @RequestBody AdminUpdateAiSuggestionStatusRequest request) {
        return ResponseEntity.ok(adminAiSuggestionService.updateStatus(id, request));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<AdminTicketDto> approveAndCreateTicket(@PathVariable Long id,
                                                                 @Valid @RequestBody AdminCreateTicketFromSuggestionRequest request) {
        AdminTicketDto ticket = adminAiSuggestionService.createTicketFromSuggestion(id, request.getCreatorUserId(), request.getAssigneeUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
}
