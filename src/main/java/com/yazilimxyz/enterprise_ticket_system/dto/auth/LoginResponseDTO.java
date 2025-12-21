package com.yazilimxyz.enterprise_ticket_system.dto.auth;

import com.yazilimxyz.enterprise_ticket_system.entities.Role;

public record LoginResponseDTO(
                Long id,
                String name,
                String surname,
                String email,
                Role role,
                String token,
                String refreshToken) {
}
