package com.example.soeiapi.entities;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "permissions", schema = "dbo")
@Data
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Integer permissionId;

    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;

    @OneToMany(mappedBy = "permission")
    private Set<RolePermissionsEntity> rolePermissions;
}
