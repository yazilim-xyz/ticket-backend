package com.example.ticketing.mapper;

import com.example.ticketing.dto.admin.AdminUpdateUserPreferenceRequest;
import com.example.ticketing.dto.admin.AdminUserPreferenceDto;
import com.example.ticketing.entity.UserPreference;

public final class AdminUserPreferenceMapper {

    private AdminUserPreferenceMapper() {
    }

    public static AdminUserPreferenceDto toDto(UserPreference preference) {
        if (preference == null) {
            return null;
        }
        return AdminUserPreferenceDto.builder()
                .userId(preference.getUser() != null ? preference.getUser().getId() : null)
                .emailNotificationsEnabled(preference.isEmailNotificationsEnabled())
                .pushNotificationsEnabled(preference.isPushNotificationsEnabled())
                .build();
    }

    public static void applyUpdate(UserPreference preference, AdminUpdateUserPreferenceRequest request) {
        preference.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        preference.setPushNotificationsEnabled(request.getPushNotificationsEnabled());
    }
}
