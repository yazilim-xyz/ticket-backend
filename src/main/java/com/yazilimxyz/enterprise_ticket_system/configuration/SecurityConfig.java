package com.yazilimxyz.enterprise_ticket_system.configuration;

import com.yazilimxyz.enterprise_ticket_system.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    // TODO application propertiesten falan alınır ki front kısmının ipsine göre
    // ayarlamak lazım zaten
    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String corsAllowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // CSRF ve default login ekranlarını kapat
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // ✅ CORS ekleyin
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT = STATELESS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Yetki kuralları
                .authorizeHttpRequests(auth -> auth

                        // Herkese açık endpointler
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/auth/refresh",
                                "/public/**",
                                "/chat-test.html",
                                "/notification-test.html",
                                // WebSocket/SockJS handshake ve yardımcı endpointleri serbest bırak
                                "/ws",
                                "/ws/**",
                                // Swagger/OpenAPI endpoints
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/configuration/**",
                                "/api-docs/**")
                        // TODO şu üst kısmın değişmesi lazım chat-test.html zaten productionda
                        // olmayacak. ws ws/** kısımları da herkese açık olmayacak sanırım. onun
                        // dışındakilere de bi bak
                        .permitAll()
                        .requestMatchers("/auth/logout").authenticated()

                        // Rol bazlı yetkiler
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")

                        // Kullanıcı listesi - kimlik doğrulaması gerekli
                        .requestMatchers("/api/users").authenticated()

                        // Chatbot endpoint - kimlik doğrulaması gerekli
                        .requestMatchers("/api/chatbot/**").authenticated()

                        // Diğer tüm endpointler → JWT zorunlu
                        .anyRequest().authenticated())

                // JWT filtresini ekle
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(corsAllowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
