package com.example.soeiapi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // Exists by username or email
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findByCompany(CompanyEntity company, Pageable pageable);

    // update last login
    @Query(value = "UPDATE users SET last_login = NOW() WHERE user_id = :userId", nativeQuery = true)
    void updateLastLogin(@Param("userId") Long userId);

    // update isEnabled
    @Query(value = "UPDATE users SET is_enabled = :isEnabled WHERE user_id = :userId", nativeQuery = true)
    void updateIsEnabled(@Param("userId") Long userId, @Param("isEnabled") Boolean isEnabled);

    @Query(value = """
            WITH UserHierarchy AS (
                SELECT user_id, parent_id, 1 AS level
                FROM users
                WHERE parent_id IS NULL

                UNION ALL

                SELECT u.user_id, u.parent_id, uh.level + 1
                FROM users u
                INNER JOIN UserHierarchy uh ON u.parent_id = uh.user_id
            )
            SELECT level FROM UserHierarchy WHERE user_id = :userId
            """, nativeQuery = true)
    Integer findUserLevel(@Param("userId") Long userId);

    @Query(value = """
            WITH UserHierarchy AS (
                SELECT id, parent_id, 1 AS level
                FROM users
                WHERE id = :userId

                UNION ALL

                SELECT u.id, u.parent_id, uh.level + 1
                FROM users u
                INNER JOIN UserHierarchy uh ON u.parent_id = uh.id
            )
            SELECT u.* FROM users u
            INNER JOIN UserHierarchy uh ON u.id = uh.id
            WHERE uh.level > (SELECT level FROM UserHierarchy WHERE id = :userId)
            """, nativeQuery = true)
    List<UserEntity> findAllChildUsersExcludingSameLevel(@Param("userId") Long userId);

}
