package com.yazilimxyz.enterprise_ticket_system.bootstrap;

import com.yazilimxyz.enterprise_ticket_system.entities.User;
import com.yazilimxyz.enterprise_ticket_system.entities.UserPreference;
import com.yazilimxyz.enterprise_ticket_system.repository.UserRepository;
import com.yazilimxyz.enterprise_ticket_system.Repositories.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(5) // Kullanıcılardan sonra
public class UserPreferenceSeeder implements CommandLineRunner {

    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Value("${app.seed.enabled:true}")
    private boolean enabled;

    @Override
    public void run(String... args) {
        if (!enabled) {
            log.debug("[UserPreferenceSeeder] Seeding disabled");
            return;
        }

        if (preferenceRepository.count() > 0) {
            log.debug("[UserPreferenceSeeder] User preferences already seeded");
            return;
        }

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.warn("[UserPreferenceSeeder] No users found");
            return;
        }

        List<UserPreference> preferences = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        String[] languages = { "tr", "en", "tr", "en", "tr" }; // Türkçe ve İngilizce karışık
        String[] themes = { "light", "dark", "light", "dark", "light" }; // Light ve Dark karışık
        int langIndex = 0;
        int themeIndex = 0;

        for (User user : users) {
            UserPreference pref = new UserPreference();
            pref.setUser(user);
            pref.setLanguage(languages[langIndex % languages.length]);
            pref.setThemeColor(themes[themeIndex % themes.length]);

            // Admin'ler tüm bildirimleri açık
            // Normal user'lar çoğunluk açık ama bazıları kapalı
            if (user.getRole().name().equals("ADMIN")) {
                pref.setNotificationPref(true);
            } else {
                pref.setNotificationPref(themeIndex % 3 != 0); // Her 3'te biri kapalı
            }

            pref.setUpdatedAt(now);
            preferences.add(pref);

            langIndex++;
            themeIndex++;
        }

        preferenceRepository.saveAll(preferences);
        log.info("[UserPreferenceSeeder] Successfully seeded {} user preferences", preferences.size());
    }
}
