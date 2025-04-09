package com.ardnaxela.library_management_system.Components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${JWT_SECRET}") // From .env or application.properties
    private String jwtSecret;
    @Value("${JWT_EXPIRATION}") // From .env or application.properties
    private int jwtExpirationMs ; // 12 Minutes
    private Key key;

    @PostConstruct // Initialize the Key after 'jwtSecret' is injected
    public void init() {
        // Ensure the secret is at least 256 bits (32 bytes) for HS256
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 32 characters long");
        }
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Generate a JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract username from JWT token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}