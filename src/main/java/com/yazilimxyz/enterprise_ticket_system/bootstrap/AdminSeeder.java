package com.yazilimxyz.enterprise_ticket_system.bootstrap;

import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@Slf4j
@Order(0) // En önce admin oluşturulmalı
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.enabled:true}")
    private boolean enabled;

    @Value("${app.seed.admin.email:admin@local}")
    private String email;

    @Value("${app.seed.admin.password:Admin123!}")
    private String password;

    @Value("${app.seed.admin.name:Admin}")
    private String name;

    @Value("${app.seed.admin.surname:User}")
    private String surname;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            log.debug("[AdminSeeder] Seeding disabled");
            return;
        }

        userRepository.findByEmail(email).ifPresentOrElse(existing -> {
            boolean looksEncoded = existing.getPasswordHash() != null
                    && (existing.getPasswordHash().startsWith("$2")
                            || existing.getPasswordHash().startsWith("{bcrypt}"));
            if (!looksEncoded) {
                existing.setPasswordHash(passwordEncoder.encode(existing.getPasswordHash()));
                existing.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                userRepository.save(existing);
                log.info("[AdminSeeder] Encoded existing admin password for {}", email);
            } else {
                log.debug("[AdminSeeder] Admin already exists with encoded password: {}", email);
            }
            if (!existing.isActive()) {
                existing.setActive(true);
                existing.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                userRepository.save(existing);
                log.info("[AdminSeeder] Reactivated admin account {}", email);
            }
            if (!existing.isApproved()) {
                existing.setApproved(true);
                existing.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                userRepository.save(existing);
                log.info("[AdminSeeder] Approved admin account {}", email);
            }
        }, () -> {
            User u = new User();
            u.setEmail(email);
            u.setName(name);
            u.setSurname(surname);
            u.setPasswordHash(passwordEncoder.encode(password));
            u.setRole(Role.ADMIN);
            u.setActive(true);
            u.setApproved(true);
            u.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            u.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

            userRepository.save(u);
            log.info("[AdminSeeder] Admin created: {}", email);
        });
    }
}
