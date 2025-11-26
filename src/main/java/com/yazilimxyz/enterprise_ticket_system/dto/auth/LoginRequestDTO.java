package com.yazilimxyz.enterprise_ticket_system.dto.auth;

public record LoginRequestDTO(
        String email,
        String password
) {}
