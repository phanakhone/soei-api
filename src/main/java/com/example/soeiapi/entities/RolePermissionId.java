package com.example.soeiapi.entities;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class RolePermissionId implements Serializable {
    private Integer roleId;
    private Integer permissionId;
}
