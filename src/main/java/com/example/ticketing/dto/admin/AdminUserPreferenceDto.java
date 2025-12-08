package com.example.ticketing.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserPreferenceDto {
    private Long userId;
    private boolean emailNotificationsEnabled;
    private boolean pushNotificationsEnabled;
}
