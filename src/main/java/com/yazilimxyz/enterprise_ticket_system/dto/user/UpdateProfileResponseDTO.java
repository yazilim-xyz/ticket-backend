package com.yazilimxyz.enterprise_ticket_system.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileResponseDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String message;
}
