package com.yazilimxyz.enterprise_ticket_system.controller.user;

import com.yazilimxyz.enterprise_ticket_system.dto.user.ChangePasswordDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.user.UpdateProfileDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.user.UpdateProfileResponseDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.user.UserListItemDto;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Sadece aktif(soft delete olmamış) kullanıcıları listele.
     */
    @GetMapping
    public ResponseEntity<List<UserListItemDto>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    /**
     * Kendi profilini güncelle (email, isim, soyisim, telefon)
     */
    @PatchMapping("/profile")
    public ResponseEntity<UpdateProfileResponseDTO> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileDTO dto) {
        UpdateProfileResponseDTO response = userService.updateProfile(user.getId(), dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Şifre değiştir (eski şifre doğrulaması yapılır)
     */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(user.getId(), dto);
        return ResponseEntity.noContent().build();
    }
}
