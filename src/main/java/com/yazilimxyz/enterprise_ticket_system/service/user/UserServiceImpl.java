package com.yazilimxyz.enterprise_ticket_system.service.user;

import com.yazilimxyz.enterprise_ticket_system.dto.user.ChangePasswordDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.user.UpdateProfileDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.user.UpdateProfileResponseDTO;
import com.yazilimxyz.enterprise_ticket_system.dto.user.UserListItemDto;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.exception.BadRequestException;
import com.yazilimxyz.enterprise_ticket_system.exception.NotFoundException;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserListItemDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToUserListItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserListItemDto> getActiveUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(User::isActive)
                .map(this::mapToUserListItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UpdateProfileResponseDTO updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));

        // Email değiştiriliyorsa, başka bir kullanıcı tarafından kullanılmadığını
        // kontrol et
        if (dto.getEmail() != null && !user.getEmail().equals(dto.getEmail())) {
            userRepository.findByEmail(dto.getEmail()).ifPresent(existingUser -> {
                throw new BadRequestException("Bu email zaten kullanımda");
            });
        }

        // Sadece gönderilen alanları güncelle
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getSurname() != null && !dto.getSurname().isBlank()) {
            user.setSurname(dto.getSurname());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        user.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        User updatedUser = userRepository.save(user);

        return UpdateProfileResponseDTO.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .surname(updatedUser.getSurname())
                .email(updatedUser.getEmail())
                .phoneNumber(updatedUser.getPhoneNumber())
                .message("Profil başarıyla güncellendi")
                .build();
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Kullanıcı bulunamadı"));

        // Eski şifre doğrulama
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Eski şifre yanlış");
        }

        // Yeni şifre ve doğrulama alanı eşit mi kontrol et
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Yeni şifreler eşleşmiyor");
        }

        // Eski şifre ile yeni şifre aynı mı kontrol et
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Yeni şifre eski şifreden farklı olmalıdır");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        userRepository.save(user);
    }

    private UserListItemDto mapToUserListItemDto(User user) {
        return UserListItemDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
