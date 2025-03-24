package com.example.soeiapi.entities;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UserPermissionId implements Serializable {
    private Integer userId;
    private Integer permissionId;

}
