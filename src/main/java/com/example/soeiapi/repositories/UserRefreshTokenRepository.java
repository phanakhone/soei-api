package com.example.soeiapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.entities.UserRefreshTokenEntity;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshTokenEntity, Long> {
    Optional<UserRefreshTokenEntity> findByToken(String token);

    void deleteByUser(UserEntity user);
}
