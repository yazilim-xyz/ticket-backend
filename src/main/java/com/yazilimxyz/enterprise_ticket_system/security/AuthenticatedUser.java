package com.yazilimxyz.enterprise_ticket_system.security;

import java.security.Principal;

import com.yazilimxyz.enterprise_ticket_system.entities.Role;

public record AuthenticatedUser(Long id, String email, Role role) implements Principal {
    @Override
    public String getName() {
        return id != null ? String.valueOf(id) : "";
    }
}
