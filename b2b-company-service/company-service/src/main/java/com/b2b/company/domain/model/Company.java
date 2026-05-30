package com.b2b.company.domain.model;

import com.b2b.company.domain.enumtype.CompanyStatus;
import com.b2b.company.domain.enumtype.CompanyType;
import com.b2b.company.domain.exception.CompanyDomainException;
import com.b2b.company.domain.valueobject.TaxNumber;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Company {

    private final UUID id;
    private String name;
    private TaxNumber taxNumber;
    private CompanyType companyType;
    private CompanyStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Company(
            UUID id,
            String name,
            TaxNumber taxNumber,
            CompanyType companyType,
            CompanyStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateName(name);
        validateCompanyType(companyType);
        validateStatus(status);

        this.id = Objects.requireNonNull(id, "Company id cannot be null.");
        this.name = name.trim();
        this.taxNumber = Objects.requireNonNull(taxNumber, "Tax number cannot be null.");
        this.companyType = companyType;
        this.status = status;
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null.");
        this.updatedAt = updatedAt;
    }

    public static Company create(
            String name,
            TaxNumber taxNumber,
            CompanyType companyType
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new Company(
                UUID.randomUUID(),
                name,
                taxNumber,
                companyType,
                CompanyStatus.ACTIVE,
                now,
                now
        );
    }

    public static Company restore(
            UUID id,
            String name,
            TaxNumber taxNumber,
            CompanyType companyType,
            CompanyStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Company(
                id,
                name,
                taxNumber,
                companyType,
                status,
                createdAt,
                updatedAt
        );
    }

    public void updateDetails(
            String name,
            TaxNumber taxNumber,
            CompanyType companyType
    ) {
        validateName(name);
        validateCompanyType(companyType);

        this.name = name.trim();
        this.taxNumber = Objects.requireNonNull(taxNumber, "Tax number cannot be null.");
        this.companyType = companyType;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status == CompanyStatus.ACTIVE) {
            throw new CompanyDomainException("Company is already active.");
        }

        this.status = CompanyStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status == CompanyStatus.INACTIVE) {
            throw new CompanyDomainException("Company is already inactive.");
        }

        this.status = CompanyStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == CompanyStatus.ACTIVE;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new CompanyDomainException("Company name cannot be blank.");
        }

        if (name.trim().length() < 2) {
            throw new CompanyDomainException("Company name must contain at least 2 characters.");
        }

        if (name.trim().length() > 150) {
            throw new CompanyDomainException("Company name cannot exceed 150 characters.");
        }
    }

    private void validateCompanyType(CompanyType companyType) {
        if (companyType == null) {
            throw new CompanyDomainException("Company type cannot be null.");
        }
    }

    private void validateStatus(CompanyStatus status) {
        if (status == null) {
            throw new CompanyDomainException("Company status cannot be null.");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaxNumber getTaxNumber() {
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