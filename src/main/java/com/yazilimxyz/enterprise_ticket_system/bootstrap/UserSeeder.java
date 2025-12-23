package com.yazilimxyz.enterprise_ticket_system.bootstrap;

import com.yazilimxyz.enterprise_ticket_system.entities.Role;
import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // İlk önce kullanıcılar oluşturulmalı
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean enabled;

    @Override
    public void run(String... args) {
        if (!enabled) {
            log.debug("[UserSeeder] Seeding disabled");
            return;
        }

        if (userRepository.count() > 1) { // AdminSeeder'dan gelen 1 admin var
            log.debug("[UserSeeder] Users already seeded");
            return;
        }

        List<User> users = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Admin Kullanıcılar (Ticket oluşturur, user'lara atar)
        users.add(createUser(
                "Ahmet", "Yılmaz", "ahmet.yilmaz@enterprise.com", "+905551234567",
                Role.ADMIN, now.minusMonths(12)));

        users.add(createUser(
                "Elif", "Kaya", "elif.kaya@enterprise.com", "+905551234568",
                Role.ADMIN, now.minusMonths(10)));

        // User'lar (Kendilerine atanan ticket'ları çözer)
        users.add(createUser(
                "Mehmet", "Demir", "mehmet.demir@enterprise.com", "+905551234569",
                Role.USER, now.minusMonths(8)));

        users.add(createUser(
                "Zeynep", "Çelik", "zeynep.celik@enterprise.com", "+905551234570",
                Role.USER, now.minusMonths(7)));

        users.add(createUser(
                "Can", "Öztürk", "can.ozturk@enterprise.com", "+905551234571",
                Role.USER, now.minusMonths(6)));

        users.add(createUser(
                "Ayşe", "Şahin", "ayse.sahin@enterprise.com", "+905551234572",
                Role.USER, now.minusMonths(5)));

        users.add(createUser(
                "Burak", "Yıldız", "burak.yildiz@enterprise.com", "+905551234573",
                Role.USER, now.minusMonths(4)));

        users.add(createUser(
                "Selin", "Aydın", "selin.aydin@enterprise.com", "+905551234574",
                Role.USER, now.minusMonths(3)));

        users.add(createUser(
                "Emre", "Koç", "emre.koc@enterprise.com", "+905551234575",
                Role.USER, now.minusMonths(2)));

        users.add(createUser(
                "Deniz", "Arslan", "deniz.arslan@enterprise.com", "+905551234576",
                Role.USER, now.minusMonths(1)));

        users.add(createUser(
                "Fatma", "Güneş", "fatma.gunes@enterprise.com", "+905551234577",
                Role.USER, now.minusWeeks(3)));

        users.add(createUser(
                "Oğuz", "Polat", "oguz.polat@enterprise.com", "+905551234578",
                Role.USER, now.minusWeeks(2)));

        users.add(createUser(
                "Merve", "Kurt", "merve.kurt@enterprise.com", "+905551234579",
                Role.USER, now.minusWeeks(1)));

        users.add(createUser(
                "Ali", "Akar", "ali.akar@enterprise.com", "+905551234580",
                Role.USER, now.minusDays(5)));

        users.add(createUser(
                "Ceren", "Yurt", "ceren.yurt@enterprise.com", "+905551234581",
                Role.USER, now.minusDays(3)));

        userRepository.saveAll(users);
        log.info("[UserSeeder] Successfully seeded {} users", users.size());
    }

    private User createUser(String name, String surname, String email, String phone,
            Role role, LocalDateTime createdAt) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPasswordHash(passwordEncoder.encode("Pass123!")); // Hepsi aynı şifre (demo için)
        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(createdAt);
        return user;
    }
}
