package com.example.soeiapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.soeiapi.entities.CompanyEntity;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Integer> {
    Optional<CompanyEntity> findByCompanyName(String companyName);

}
