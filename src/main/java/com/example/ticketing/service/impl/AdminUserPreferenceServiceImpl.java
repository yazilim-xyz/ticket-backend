package com.example.ticketing.service.impl;

import com.example.ticketing.dto.admin.AdminUpdateUserPreferenceRequest;
import com.example.ticketing.dto.admin.AdminUserPreferenceDto;
import com.example.ticketing.entity.User;
import com.example.ticketing.entity.UserPreference;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.mapper.AdminUserPreferenceMapper;
import com.example.ticketing.repository.UserPreferenceRepository;
import com.example.ticketing.repository.UserRepository;
import com.example.ticketing.service.admin.AdminUserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserPreferenceServiceImpl implements AdminUserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;

    @Override
    public AdminUserPreferenceDto getPreferences(Long userId) {
        UserPreference preference = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreference(userId));
        return AdminUserPreferenceMapper.toDto(preference);
    }

    @Override
    public AdminUserPreferenceDto updatePreferences(Long userId, AdminUpdateUserPreferenceRequest request) {
        UserPreference preference = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreference(userId));
        AdminUserPreferenceMapper.applyUpdate(preference, request);
        UserPreference saved = userPreferenceRepository.save(preference);
        return AdminUserPreferenceMapper.toDto(saved);
    }

    private UserPreference createDefaultPreference(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        return userPreferenceRepository.save(UserPreference.builder()
                .user(user)
                .build());
    }
}
