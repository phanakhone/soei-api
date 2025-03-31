package com.example.soeiapi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.entities.UserEntity;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // Exists by username or email
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findByCompany(CompanyEntity company, Pageable pageable);

    // get latest user by companyId and roleId
    @Query(value = "select top 1 u.* from users u inner join user_roles ur on u.user_id = ur.user_id where ur.role_id = :roleId and u.company_id = :companyId and len(u.username) > 7 order by u.username desc", nativeQuery = true)
    Optional<UserEntity> findLatestUserByCompanyIdAndRoleId(@Param("companyId") Long companyId,
            @Param("roleId") Integer roleId);

    // update last login
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = :userId", nativeQuery = true)
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

    @Query(value = "WITH UserHierarchy AS (SELECT user_id, parent_id, 1 AS level FROM users WHERE user_id = :userId UNION ALL SELECT u.user_id, u.parent_id, uh.level + 1 FROM users u INNER JOIN UserHierarchy uh ON u.parent_id = uh.user_id) SELECT u.* FROM users u INNER JOIN UserHierarchy uh ON u.user_id = uh.user_id WHERE uh.level > (SELECT level FROM UserHierarchy WHERE user_id = :userId)", nativeQuery = true)
    Page<UserEntity> findAllChildUsersExcludingSameLevel(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT c.company_short_name, r.role_name AS role_name, COUNT(u.company_id) AS total " +
            "FROM users u " +
            "INNER JOIN user_roles ur ON ur.user_id = u.user_id " +
            "INNER JOIN companies c ON c.company_id = u.company_id " +
            "INNER JOIN roles r ON ur.role_id = r.role_id " +
            "GROUP BY c.company_short_name, r.role_name", nativeQuery = true)
    List<Object[]> countUsersByCompanyAndRole();
}
