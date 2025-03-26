package com.example.soeiapi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "permissions", schema = "dbo")
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long permissionId;

    @Column(name = "permission_name", nullable = false, unique = true)
    private String permissionName;

    // @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval
    // = true)
    // private Set<RolePermissionsEntity> rolePermissions;

    // @OneToMany(mappedBy = "permission")
    // private Set<RolePermissionsEntity> rolePermissions;

    // @ManyToMany(mappedBy = "permissions")
    // private Set<RoleEntity> roles = new HashSet<>();
}
