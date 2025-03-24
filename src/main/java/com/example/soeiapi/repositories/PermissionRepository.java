package com.example.soeiapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.soeiapi.entities.PermissionEntity;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> {

}
