package com.yazilimxyz.enterprise_ticket_system.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    // En az 32 karakter zorunlu
    private final String SECRET_KEY = "supersecretkeysupersecretkey1234567890";
    private final long EXPIRATION = 1000 * 60 * 60; // 1 saat

    // TOKEN ÜRETME
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)                 // subject = email
                .claim("role", role)               // token içine rol ekle
                .setIssuedAt(new Date())           // oluşturulma zamanı
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    // TOKEN'DAN EMAIL ÇEK
    public String extractEmail(String token) {
        return getAllClaims(token).getSubject();
    }

    // TOKEN'DAN ROLE ÇEK
    public String extractRole(String token) {
        return (String) getAllClaims(token).get("role");
    }

    // Genel claim’lere erişim
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Token süresi dolmuş mu?
    public boolean isTokenExpired(String token) {
        Date expiration = getAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // Basit doğrulama: email tutuyor mu ve süresi dolmamış mı
    public boolean isTokenValid(String token, String email) {
        String tokenEmail = extractEmail(token);
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }
}
