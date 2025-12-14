package com.example.ticketing.controller.admin;

import com.example.ticketing.dto.admin.AdminTicketNotificationDto;
import com.example.ticketing.service.admin.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @GetMapping
    public ResponseEntity<List<AdminTicketNotificationDto>> getNotifications(@RequestParam(required = false) Long userId,
                                                                             @RequestParam(required = false) Long ticketId) {
        return ResponseEntity.ok(adminNotificationService.getNotifications(userId, ticketId));
    }
}
