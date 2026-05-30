package com.b2b.company.domain.model;

import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;
import com.b2b.company.domain.exception.CompanyMemberDomainException;
import com.b2b.company.domain.valueobject.Email;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CompanyMember {

    private final UUID id;
    private final UUID companyId;
    private String fullName;
    private Email email;
    private CompanyMemberRole role;
    private CompanyMemberStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private CompanyMember(
            UUID id,
            UUID companyId,
            String fullName,
            Email email,
            CompanyMemberRole role,
            CompanyMemberStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateFullName(fullName);
        validateRole(role);
        validateStatus(status);

        this.id = Objects.requireNonNull(id, "Company member id cannot be null.");
        this.companyId = Objects.requireNonNull(companyId, "Company id cannot be null.");
        this.fullName = fullName.trim();
        this.email = Objects.requireNonNull(email, "Email cannot be null.");
        this.role = role;
        this.status = status;
        this.createdAt = Objects.requireNonNull(createdAt, "Created date cannot be null.");
        this.updatedAt = updatedAt;
    }

    public static CompanyMember create(
            UUID companyId,
            String fullName,
            Email email,
            CompanyMemberRole role
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new CompanyMember(
                UUID.randomUUID(),
                companyId,
                fullName,
                email,
                role,
                CompanyMemberStatus.ACTIVE,
                now,
                now
        );
    }

    public static CompanyMember restore(
            UUID id,
            UUID companyId,
            String fullName,
            Email email,
            CompanyMemberRole role,
            CompanyMemberStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new CompanyMember(
                id,
                companyId,
                fullName,
                email,
                role,
                status,
                createdAt,
                updatedAt
        );
    }

    public void updateProfile(
            String fullName,
            Email email,
            CompanyMemberRole role
    ) {
        validateFullName(fullName);
        validateRole(role);

        this.fullName = fullName.trim();
        this.email = Objects.requireNonNull(email, "Email cannot be null.");
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status == CompanyMemberStatus.ACTIVE) {
            throw new CompanyMemberDomainException("Company member is already active.");
        }

        this.status = CompanyMemberStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status == CompanyMemberStatus.INACTIVE) {
            throw new CompanyMemberDomainException("Company member is already inactive.");
        }

        this.status = CompanyMemberStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == CompanyMemberStatus.ACTIVE;
    }

    private void validateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new CompanyMemberDomainException("Company member full name cannot be blank.");
        }

        if (fullName.trim().length() < 2) {
            throw new CompanyMemberDomainException("Company member full name must contain at least 2 characters.");
        }

        if (fullName.trim().length() > 100) {
            throw new CompanyMemberDomainException("Company member full name cannot exceed 100 characters.");
        }
    }

    private void validateRole(CompanyMemberRole role) {
        if (role == null) {
            throw new CompanyMemberDomainException("Company member role cannot be null.");
        }
    }

    private void validateStatus(CompanyMemberStatus status) {
        if (status == null) {
            throw new CompanyMemberDomainException("Company member status cannot be null.");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getFullName() {
        return fullName;
    }

    public Email getEmail() {
        return email;
    }

    public CompanyMemberRole getRole() {
        return role;
    }

    public CompanyMemberStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}