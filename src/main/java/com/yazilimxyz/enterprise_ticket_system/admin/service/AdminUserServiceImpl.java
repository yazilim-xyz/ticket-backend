package com.yazilimxyz.enterprise_ticket_system.admin.service;

import com.yazilimxyz.enterprise_ticket_system.admin.dto.user.*;
import com.yazilimxyz.enterprise_ticket_system.admin.exception.NotFoundException;
import com.yazilimxyz.enterprise_ticket_system.admin.mapper.AdminUserMapper;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepo;
    private final AdminUserMapper mapper;
    private final PasswordEncoder encoder;

    @Override
    public Page<AdminUserResponseDto> getUsers(int page, int size) {
        Page<User> users = userRepo.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        return users.map(mapper::toDto);
    }

    @Override
    public AdminUserResponseDto getUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return mapper.toDto(user);
    }

    @Override
    public AdminUserResponseDto createUser(AdminUserCreateRequest req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());

        // tablo: password_hash
        user.setPasswordHash(encoder.encode(req.getPassword()));

        // role String
        if (req.getRole() != null) {
            user.setRole(req.getRole().toUpperCase());
        }

        // department, active vs yok → şimdilik eklemiyoruz

        User saved = userRepo.save(user);
        return mapper.toDto(saved);
    }

    @Override
    public AdminUserResponseDto updateUser(Long id, AdminUserUpdateRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        mapper.updateFromRequest(req, user);

        User updated = userRepo.save(user);
        return mapper.toDto(updated);
    }

    @Override
    public void changeUserStatus(Long id, ChangeUserStatusRequest req) {
        // Şu an User entity'de status/active alanı yok.
        // Yine de user var mı kontrol edelim, yoksa 404 fırlatalım.
        userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        // Buraya ileride status/active alanı eklersen:
        // user.setActive("ACTIVE".equalsIgnoreCase(req.getStatus()));
        // userRepo.save(user);
    }

    @Override
    public void changeUserRole(Long id, ChangeUserRoleRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        user.setRole(req.getRole().toUpperCase());
        userRepo.save(user);
    }
}
