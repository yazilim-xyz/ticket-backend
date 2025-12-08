package com.example.ticketing.service.admin;

import com.example.ticketing.dto.admin.AdminUpdateUserPreferenceRequest;
import com.example.ticketing.dto.admin.AdminUserPreferenceDto;

public interface AdminUserPreferenceService {

    AdminUserPreferenceDto getPreferences(Long userId);

    AdminUserPreferenceDto updatePreferences(Long userId, AdminUpdateUserPreferenceRequest request);
}
