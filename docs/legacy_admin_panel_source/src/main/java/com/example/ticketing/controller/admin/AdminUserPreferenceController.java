package com.example.ticketing.controller.admin;

import com.example.ticketing.dto.admin.AdminUpdateUserPreferenceRequest;
import com.example.ticketing.dto.admin.AdminUserPreferenceDto;
import com.example.ticketing.service.admin.AdminUserPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users/{id}/preferences")
@RequiredArgsConstructor
public class AdminUserPreferenceController {

    private final AdminUserPreferenceService adminUserPreferenceService;

    @GetMapping
    public ResponseEntity<AdminUserPreferenceDto> getPreferences(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserPreferenceService.getPreferences(id));
    }

    @PutMapping
    public ResponseEntity<AdminUserPreferenceDto> updatePreferences(@PathVariable Long id,
                                                                    @Valid @RequestBody AdminUpdateUserPreferenceRequest request) {
        return ResponseEntity.ok(adminUserPreferenceService.updatePreferences(id, request));
    }
}
