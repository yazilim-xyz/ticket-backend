package com.yazilimxyz.enterprise_ticket_system.dto.auth;

import com.yazilimxyz.enterprise_ticket_system.entities.Role;

public record RegisterResponseDTO(
                Long id,
                String name,
                String surname,
                String email,
                Role role) {
}
// Backend’in register işleminden sonra frontend’e gönderdiği veri.