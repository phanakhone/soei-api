package com.example.soeiapi.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.entities.UserRefreshTokenEntity;
import com.example.soeiapi.repositories.UserRefreshTokenRepository;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshTokenExpiration;

    @Autowired
    private UserRefreshTokenRepository userRefreshTokenRepository;

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, String> extraClaims, String username) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public String getUserName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims extractAllClaims(String token) {
        // Extract claims after signature verification
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // refresh token
    @Transactional
    public String createUserRefreshToken(UserEntity user) {
        userRefreshTokenRepository.deleteByUser(user);

        UserRefreshTokenEntity userRefreshTokenEntity = new UserRefreshTokenEntity();
        userRefreshTokenEntity.setUser(user);
        userRefreshTokenEntity.setToken(UUID.randomUUID().toString());
        userRefreshTokenEntity.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));
        userRefreshTokenRepository.save(userRefreshTokenEntity);

        return userRefreshTokenEntity.getToken();
    }

    public UserRefreshTokenEntity validateRefreshToken(String token) {
        UserRefreshTokenEntity userRefreshToken = userRefreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (userRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            // delete the token
            userRefreshTokenRepository.delete(userRefreshToken);
            throw new RuntimeException("Refresh token has expired");
        }

        return userRefreshToken;
    }
}
