package com.yazilimxyz.enterprise_ticket_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
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
        byte[] secretBytes = decodeSecret(secret);
        log.info("[JwtUtil] Using JWT secret hash prefix={} (len={} bytes)", sha256Prefix(secretBytes),
                secretBytes.length);
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.expirationMs = expirationMs;
        this.issuer = issuer;
        this.audience = audience;
    }

    public String generateToken(Long userId, String email, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        // TODO burada neden random uuid kullanılıyor?? userId kullanılması daha
        // mantıklı değil mi? userIdyi guid kullanacak şekilde ayarlayıp onu kullanırız.
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

    private byte[] decodeSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("jwt.secret must not be blank");
        }
        String trimmed = secret.trim();
        byte[] raw;
        if (trimmed.matches("^[0-9a-fA-F]+$") && trimmed.length() % 2 == 0) {
            raw = hexToBytes(trimmed);
        } else {
            raw = trimmed.getBytes(StandardCharsets.UTF_8);
        }
        if (raw.length < 32) { // HS256 needs >= 256 bits
            throw new IllegalArgumentException("jwt.secret must be at least 32 bytes (256 bits)");
        }
        return raw;
    }

    private String sha256Prefix(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            return bytesToHex(digest).substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
