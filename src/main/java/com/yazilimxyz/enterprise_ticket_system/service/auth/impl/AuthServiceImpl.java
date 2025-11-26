package com.yazilimxyz.enterprise_ticket_system.service.auth.impl;

import com.yazilimxyz.enterprise_ticket_system.dto.auth.*;
import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.security.JwtUtil;
import com.yazilimxyz.enterprise_ticket_system.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO request) {

        // 1) Email zaten var mı?
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use!");
        }

        // 2) Yeni kullanıcı oluştur
        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER); // default: USER
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 3) Kaydet
        User saved = userRepository.save(user);

        // 4) DTO dön
        return new RegisterResponseDTO(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole()
        );
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        // 1) Kullanıcı var mı?
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // 2) Şifre doğru mu?
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 3) JWT TOKEN ÜRET (GERÇEK TOKEN)
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        // 4) DTO dön
        return new LoginResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                token
        );
    }
}
