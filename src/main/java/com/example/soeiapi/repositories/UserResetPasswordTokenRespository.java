package com.example.soeiapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.soeiapi.entities.UserResetPasswordTokenEntity;

import jakarta.transaction.Transactional;

@Repository
public interface UserResetPasswordTokenRespository extends JpaRepository<UserResetPasswordTokenEntity, Long> {
    Optional<UserResetPasswordTokenEntity> findByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserResetPasswordTokenEntity urpt WHERE urpt.user.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}
