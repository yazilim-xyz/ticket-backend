package com.yazilimxyz.enterprise_ticket_system.service.auth.impl;

import com.yazilimxyz.enterprise_ticket_system.dto.auth.*;
import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.RefreshToken;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.exception.BadRequestException;
import com.yazilimxyz.enterprise_ticket_system.exception.ForbiddenException;
import com.yazilimxyz.enterprise_ticket_system.exception.TooManyRequestsException;
import com.yazilimxyz.enterprise_ticket_system.exception.UnauthorizedException;
import com.yazilimxyz.enterprise_ticket_system.repository.RefreshTokenRepository;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.security.JwtUtil;
import com.yazilimxyz.enterprise_ticket_system.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    // Business logic for auth
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh-days:30}")
    private long refreshTokenDays;

    @Value("${auth.login.max-attempts:5}")
    private int maxLoginAttempts;

    @Value("${auth.login.block-minutes:15}")
    private long blockMinutes;

    private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO request) {

        // 1) Check existing email
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use");
        }

        // 2) Create user
        User user = new User();
        user.setName(request.name());
        user.setSurname(request.surname());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER); // default: USER
        user.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        user.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        user.setActive(true);
        user.setApproved(false); // Requires admin approval

        // 3) Save
        User saved = userRepository.save(user);

        // 4) DTO
        return new RegisterResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getSurname(),
                saved.getEmail(),
                saved.getRole());
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        checkRateLimit(request.email());

        // 1) Find user
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    recordFailedAttempt(request.email());
                    return new UnauthorizedException("Invalid email or password");
                });

        // 2) Block if disabled
        if (!user.isActive()) {
            throw new ForbiddenException("Account disabled");
        }

        // 2.5) Check if approved by admin
        if (!user.isApproved()) {
            throw new ForbiddenException("Account pending admin approval");
        }

        // 3) Verify password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            recordFailedAttempt(request.email());
            throw new UnauthorizedException("Invalid email or password");
        }

        resetAttempts(request.email());

        // 4) Issue JWT + Refresh
        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name());
        RefreshToken refreshToken = createRefreshToken(user);

        // 5) DTO
        return new LoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getRole(),
                token,
                refreshToken.getToken());
    }

    @Override
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (stored.isRevoked()) {
            throw new UnauthorizedException("Refresh token revoked");
        }
        if (stored.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = stored.getUser();
        if (!user.isActive()) {
            throw new ForbiddenException("Account disabled");
        }

        // Rotate refresh token
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        RefreshToken newRefresh = createRefreshToken(user);

        String newAccess = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name());

        return new LoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getRole(),
                newAccess,
                newRefresh.getToken());
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusDays(refreshTokenDays));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void logout(Long userId) {
        refreshTokenRepository.findAllByUserId(userId).forEach(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    private void checkRateLimit(String email) {
        LoginAttempt attempt = loginAttempts.get(email);
        if (attempt == null) {
            return;
        }
        if (attempt.isWindowExpired(blockMinutes)) {
            loginAttempts.remove(email);
            return;
        }
        if (attempt.getCount() >= maxLoginAttempts) {
            throw new TooManyRequestsException("Too many failed login attempts. Try again later.");
        }
    }

    private void recordFailedAttempt(String email) {
        loginAttempts.compute(email, (key, attempt) -> {
            if (attempt == null || attempt.isWindowExpired(blockMinutes)) {
                return new LoginAttempt(1, OffsetDateTime.now(ZoneOffset.UTC));
            }
            return new LoginAttempt(attempt.getCount() + 1, attempt.getFirstAttemptAt());
        });
    }

    private void resetAttempts(String email) {
        loginAttempts.remove(email);
    }

    private record LoginAttempt(int count, OffsetDateTime firstAttemptAt) {
        boolean isWindowExpired(long blockMinutes) {
            return firstAttemptAt.plus(Duration.ofMinutes(blockMinutes)).isBefore(OffsetDateTime.now(ZoneOffset.UTC));
        }

        int getCount() {
            return count;
        }

        OffsetDateTime getFirstAttemptAt() {
            return firstAttemptAt;
        }
    }
}
