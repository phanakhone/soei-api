package com.example.soeiapi.entities;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "companies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "company_id")
    private int companyId;

    @Column(name = "company_name", nullable = false, unique = true, length = 255)
    private String companyName;

    @Column(name = "company_address", length = 255)
    private String companyAddress;

    @Column(name = "company_phone_number", length = 20)
    private String companyPhoneNumber;

    @Column(name = "company_email", length = 255)
    private String companyEmail;

    @Column(name = "company_website", length = 255)
    private String companyWebsite;

    @Column(name = "company_hotline_sale", length = 20)
    private String companyHotlineSale;

    @Column(name = "company_hotline_accident", length = 20)
    private String companyHotlineAccident;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
