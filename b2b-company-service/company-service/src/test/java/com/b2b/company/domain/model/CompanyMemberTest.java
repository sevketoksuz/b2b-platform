package com.b2b.company.domain.model;

import com.b2b.company.domain.enumtype.CompanyMemberRole;
import com.b2b.company.domain.enumtype.CompanyMemberStatus;
import com.b2b.company.domain.exception.CompanyMemberDomainException;
import com.b2b.company.domain.valueobject.Email;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CompanyMemberTest {

    @Test
    void shouldCreateCompanyMemberAsActive() {
        UUID companyId = UUID.randomUUID();

        CompanyMember member = CompanyMember.create(
                companyId,
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN
        );

        assertNotNull(member.getId());
        assertEquals(companyId, member.getCompanyId());
        assertEquals("John Doe", member.getFullName());
        assertEquals("john@acme.com", member.getEmail().getValue());
        assertEquals(CompanyMemberRole.ADMIN, member.getRole());
        assertEquals(CompanyMemberStatus.ACTIVE, member.getStatus());
        assertTrue(member.isActive());
        assertNotNull(member.getCreatedAt());
        assertNotNull(member.getUpdatedAt());
    }

    @Test
    void shouldUpdateCompanyMemberProfile() {
        CompanyMember member = CompanyMember.create(
                UUID.randomUUID(),
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN
        );

        member.updateProfile(
                "John Updated",
                Email.of("john.updated@acme.com"),
                CompanyMemberRole.EMPLOYEE
        );

        assertEquals("John Updated", member.getFullName());
        assertEquals("john.updated@acme.com", member.getEmail().getValue());
        assertEquals(CompanyMemberRole.EMPLOYEE, member.getRole());
    }

    @Test
    void shouldDeactivateCompanyMember() {
        CompanyMember member = CompanyMember.create(
                UUID.randomUUID(),
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN
        );

        member.deactivate();

        assertEquals(CompanyMemberStatus.INACTIVE, member.getStatus());
        assertFalse(member.isActive());
    }

    @Test
    void shouldActivateInactiveCompanyMember() {
        CompanyMember member = CompanyMember.create(
                UUID.randomUUID(),
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN
        );

        member.deactivate();
        member.activate();

        assertEquals(CompanyMemberStatus.ACTIVE, member.getStatus());
        assertTrue(member.isActive());
    }

    @Test
    void shouldThrowExceptionWhenCompanyMemberAlreadyInactive() {
        CompanyMember member = CompanyMember.create(
                UUID.randomUUID(),
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN
        );

        member.deactivate();

        CompanyMemberDomainException exception = assertThrows(
                CompanyMemberDomainException.class,
                member::deactivate
        );

        assertEquals("Company member is already inactive.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCompanyMemberAlreadyActive() {
        CompanyMember member = CompanyMember.create(
                UUID.randomUUID(),
                "John Doe",
                Email.of("john@acme.com"),
                CompanyMemberRole.ADMIN
        );

        CompanyMemberDomainException exception = assertThrows(
                CompanyMemberDomainException.class,
                member::activate
        );

        assertEquals("Company member is already active.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFullNameIsBlank() {
        CompanyMemberDomainException exception = assertThrows(
                CompanyMemberDomainException.class,
                () -> CompanyMember.create(
                        UUID.randomUUID(),
                        "",
                        Email.of("john@acme.com"),
                        CompanyMemberRole.ADMIN
                )
        );

        assertEquals("Company member full name cannot be blank.", exception.getMessage());
    }
}