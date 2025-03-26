package com.example.soeiapi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ForeignKey;

import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_permissions", schema = "dbo")
public class UserPermissionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "FK_user_permissions_user_id"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", referencedColumnName = "permission_id", nullable = false, foreignKey = @ForeignKey(name = "FK_user_permissions_permission_id"))
    private PermissionEntity permission;

}