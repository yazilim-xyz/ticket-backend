package com.yazilimxyz.enterprise_ticket_system.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
