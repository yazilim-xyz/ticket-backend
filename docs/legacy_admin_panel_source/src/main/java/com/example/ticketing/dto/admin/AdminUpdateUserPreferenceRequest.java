package com.example.ticketing.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpdateUserPreferenceRequest {

    @NotNull
    private Boolean emailNotificationsEnabled;

    @NotNull
    private Boolean pushNotificationsEnabled;
}
