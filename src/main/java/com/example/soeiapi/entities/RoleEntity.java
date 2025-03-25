package com.example.soeiapi.entities;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "role_name", nullable = false, length = 50, unique = true)
    private String roleName;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermissionsEntity> rolePermissions;
    // @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    // private Set<RolePermissionsEntity> rolePermissions;
    // @ManyToAny
    // @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name =
    // "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    // private Set<PermissionEntity> permissions = new HashSet<>();
}
