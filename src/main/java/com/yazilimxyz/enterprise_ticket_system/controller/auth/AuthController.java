package com.yazilimxyz.enterprise_ticket_system.controller.auth;

import com.yazilimxyz.enterprise_ticket_system.dto.auth.*;
import com.yazilimxyz.enterprise_ticket_system.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.yazilimxyz.enterprise_ticket_system.exception.UnauthorizedException;
import com.yazilimxyz.enterprise_ticket_system.security.AuthenticatedUser;

@RestController
@RequestMapping({"/auth", "/api/auth"})
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        RegisterResponseDTO response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        log.debug("[AuthController] /login hit for email={}", dto.email());
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    // REFRESH
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        LoginResponseDTO response = authService.refreshToken(dto);
        return ResponseEntity.ok(response);
    }

    // LOGOUT (revoke all refresh tokens for user)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        authService.logout(user.id());
        return ResponseEntity.noContent().build();
    }
}
