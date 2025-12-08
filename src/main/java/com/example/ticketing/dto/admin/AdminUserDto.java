package com.example.ticketing.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class AdminUserDto {
    private Long id;
    private String fullName;
    private String email;
    private Boolean enabled;
    private Set<String> roles;
    private Instant createdAt;
    private Instant updatedAt;
}
