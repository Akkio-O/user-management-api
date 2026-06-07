package com.server.service;

import com.server.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Service
public class JwtService {
    private final Key key =
            Keys.hmacShaKeyFor(
                    "my-super-secret-key-for-jwt-authentication-2026"
                            .getBytes()
            );
    private long expirationTime = 86400000L;

    public String extractLogin(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    };
    public boolean isTokenValid(String token) {
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

    public String generateToken(User user) {
        log.info("Процесс создания токена:");
        String jwt = Jwts.builder()
                .subject(user.getLogin())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
        log.info("JWT успешно создан для пользователя {}", user.getLogin());
        return jwt;
    }
}