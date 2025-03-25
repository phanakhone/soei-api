package com.example.soeiapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.entities.UserRefreshTokenEntity;

import jakarta.transaction.Transactional;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshTokenEntity, Long> {
    Optional<UserRefreshTokenEntity> findByToken(String token);

    Optional<UserRefreshTokenEntity> findByUser(UserEntity user);

    // delete by userId
    @Modifying
    @Transactional
    @Query("delete from UserRefreshTokenEntity urt where urt.user= :user")
    void deleteByUser(@Param("user") UserEntity user);
}
