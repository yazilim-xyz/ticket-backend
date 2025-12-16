package com.example.ticketing.service.impl;

import com.example.ticketing.dto.admin.AdminCreateUserRequest;
import com.example.ticketing.dto.admin.AdminUpdateUserRequest;
import com.example.ticketing.dto.admin.AdminUserDto;
import com.example.ticketing.entity.User;
import com.example.ticketing.exception.BusinessException;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.mapper.AdminUserMapper;
import com.example.ticketing.repository.UserRepository;
import com.example.ticketing.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(AdminUserMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return AdminUserMapper.toDto(user);
    }

    @Override
    public AdminUserDto createUser(AdminCreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use: " + request.getEmail());
        }
        User user = AdminUserMapper.fromCreateRequest(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return AdminUserMapper.toDto(saved);
    }

    @Override
    public AdminUserDto updateUser(Long id, AdminUpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Optional<User> existing = userRepository.findByEmail(request.getEmail());
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new BusinessException("Email already in use: " + request.getEmail());
            }
        }

        if (request.getEnabled() != null && Boolean.FALSE.equals(request.getEnabled())) {
            assertCanDisableOrDelete(user);
            assertNotSelf(user);
        }

        AdminUserMapper.updateEntity(user, request);
        User saved = userRepository.save(user);
        return AdminUserMapper.toDto(saved);
    }

    @Override
    public void enableUser(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (!enabled) {
            assertCanDisableOrDelete(user);
            assertNotSelf(user);
        }

        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        assertNotSelf(user);
        assertCanDisableOrDelete(user);
        userRepository.delete(user);
    }

    private void assertCanDisableOrDelete(User user) {
        if (!user.getRoles().contains(ROLE_ADMIN)) {
            return;
        }
        long otherEnabledAdmins = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .filter(u -> Boolean.TRUE.equals(u.getEnabled()))
                .filter(u -> u.getRoles().contains(ROLE_ADMIN))
                .count();
        if (otherEnabledAdmins == 0) {
            throw new BusinessException("Cannot disable/delete the last active admin user.");
        }
    }

    private void assertNotSelf(User target) {
        String currentEmail = getCurrentUserEmail();
        if (currentEmail != null && currentEmail.equalsIgnoreCase(target.getEmail())) {
            throw new BusinessException("You cannot disable or delete your own admin account.");
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }
}
