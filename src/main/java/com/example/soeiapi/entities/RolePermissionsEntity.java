package com.example.soeiapi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ForeignKey;

import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "role_permissions", schema = "dbo")
@Data
public class RolePermissionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", foreignKey = @ForeignKey(name = "FK_role_permissions_role_id"))
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "permission_id", referencedColumnName = "permission_id", foreignKey = @ForeignKey(name = "FK_role_permissions_permission_id"))
    private PermissionEntity permission;

}
