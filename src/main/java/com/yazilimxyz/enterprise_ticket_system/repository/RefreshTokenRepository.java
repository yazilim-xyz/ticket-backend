package com.yazilimxyz.enterprise_ticket_system.repository;

import com.yazilimxyz.enterprise_ticket_system.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserId(Long userId);
}
