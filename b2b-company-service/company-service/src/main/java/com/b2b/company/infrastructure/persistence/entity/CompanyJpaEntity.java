package com.b2b.company.infrastructure.persistence.entity;

import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class CompanyJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "tax_number", nullable = false, unique = true, length = 11)
    private String taxNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", nullable = false, length = 30)
    private CompanyType companyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CompanyStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CompanyJpaEntity() {
        // JPA needs a no-args constructor.
    }

    public CompanyJpaEntity(
            UUID id,
            String name,
            String taxNumber,
            CompanyType companyType,
            CompanyStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.taxNumber = taxNumber;
        this.companyType = companyType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}