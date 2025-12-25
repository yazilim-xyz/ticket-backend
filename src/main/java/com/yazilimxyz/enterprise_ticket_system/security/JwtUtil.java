package com.yazilimxyz.enterprise_ticket_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;
    private final String issuer;
    private final String audience;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs,
            @Value("${jwt.issuer:enterprise-ticket-system}") String issuer,
            @Value("${jwt.audience:enterprise-ticket-system-client}") String audience) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.issuer = issuer;
        this.audience = audience;
    }

    public String generateToken(Long userId, String email, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        // TODO burada neden random uuid kullanılıyor ve userid idye değil de subjecte
        // veriliyor???
        // o halde her yerde id için subjecti çekiyorduk onların da değiştirilmesi lazım
        // id nin kullanılması lazım.
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(String.valueOf(userId))
                .setIssuer(issuer)
                .setAudience(audience)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireAudience(audience)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, Long userId) {
        return extractUserId(token).equals(userId) && !isTokenExpired(token);
    }
}
