package com.example.soeiapi.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_permissions", schema = "dbo")
@Data
public class UserPermissionsEntity {
    @EmbeddedId
    private UserPermissionId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private PermissionEntity permission;

}