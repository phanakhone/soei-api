package com.example.soeiapi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.soeiapi.entities.RolePermissionId;
import com.example.soeiapi.entities.RolePermissionsEntity;

@Repository
public interface RolePermissionsRepository extends JpaRepository<RolePermissionsEntity, RolePermissionId> {
    // get all permission by role name
    List<RolePermissionsEntity> findByRole_RoleName(String roleName);

}
