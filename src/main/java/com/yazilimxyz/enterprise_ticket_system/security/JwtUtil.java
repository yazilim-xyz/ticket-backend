package com.yazilimxyz.enterprise_ticket_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Güvenli bir SECRET KEY (64+ karakter)
    private static final String SECRET_KEY =
            "dg7Y3hs92Kdj29Ds8sS11aaPplxo18GHs0PlqmGm392MxqAA0dkLm1Pz55Hs82jd";

    // 1 saat (ms)
    private static final long EXPIRATION = 60 * 60 * 1000;

    // TOKEN ÜRET
    public String generateToken(Long userId, String email, String role) {

        return Jwts.builder()
                .setSubject(String.valueOf(userId))     // ★ sub = userId
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    // CLAIMS AL
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // USER ID (SUB) ÇEK
    public Long extractUserId(String token) {
        return Long.valueOf(getAllClaims(token).getSubject());
    }

    // EMAIL ÇEK
    public String extractEmail(String token) {
        return getAllClaims(token).get("email", String.class);
    }

    // ROLE ÇEK
    public String extractRole(String token) {
        return getAllClaims(token).get("role", String.class);
    }

    public boolean isTokenExpired(String token) {
        return getAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, Long userId) {
        return extractUserId(token).equals(userId) && !isTokenExpired(token);
    }
}
