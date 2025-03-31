package com.example.soeiapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.soeiapi.entities.UserProfileEntity;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

    Optional<UserProfileEntity> findByUser_UserId(Long userId);

    // void deleteByUser_UserId(Long userId);

}
