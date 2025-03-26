package com.example.soeiapi.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.entities.UserRefreshTokenEntity;
import com.example.soeiapi.repositories.UserRefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class UserRefreshTokenService {
    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshTokenExpiration;

    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public UserRefreshTokenService(UserRefreshTokenRepository userRefreshTokenRepository) {
        this.userRefreshTokenRepository = userRefreshTokenRepository;
    }

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
