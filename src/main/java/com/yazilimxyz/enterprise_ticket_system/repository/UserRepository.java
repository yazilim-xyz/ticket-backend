package com.yazilimxyz.enterprise_ticket_system.repository;

import com.yazilimxyz.enterprise_ticket_system.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);// login yaparken email ile kullanıcı arayacaksın

    boolean existsByEmail(String email);// register sırasında email çakışması var mı yok mu kontrolü yapar

    Page<User> findByApproved(Boolean approved, Pageable pageable);

    Page<User> findByActive(Boolean active, Pageable pageable);

    Page<User> findByApprovedAndActive(Boolean approved, Boolean active, Pageable pageable);
}

// TODO Öneri Kontrol Listesi
// SecurityConfig → .anyRequest().authenticated() yorumdan çıkarılmalı
// WebSocketConfig → Geçersiz token durumunda exception fırlatılmalı
// Swagger'da Authorize butonu ile JWT token eklendiğinden emin olun
// Role enum değerlerine ROLE_ prefix'i eklenebilir
// @PrePersist annotasyonları TicketComment ve InternalChat'e eklenebilir
// Custom exception handler (@ControllerAdvice) eklenebilir
// Production CORS ayarları gözden geçirilmeli
