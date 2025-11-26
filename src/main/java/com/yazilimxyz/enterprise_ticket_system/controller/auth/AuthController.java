package com.yazilimxyz.enterprise_ticket_system.controller.auth;

import com.yazilimxyz.enterprise_ticket_system.dto.auth.RegisterRequestDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.auth.RegisterResponseDTO;
import com.yazilimxyz.enterprise_ticket_system.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponseDTO register(@RequestBody RegisterRequestDTO dto) {
        return authService.register(dto);
    }
}
