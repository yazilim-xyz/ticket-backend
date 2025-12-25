package com.yazilimxyz.enterprise_ticket_system.dto.admin;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private String role;
    private boolean active;
    private boolean approved;
    private String department;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastLoginAt;
}
